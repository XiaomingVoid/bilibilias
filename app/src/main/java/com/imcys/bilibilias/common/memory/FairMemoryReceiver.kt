package com.imcys.bilibilias.common.memory

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import androidx.core.content.ContextCompat
import coil3.SingletonImageLoader
import com.imcys.bilibilias.common.event.sendSaveBackStackChannel
import com.imcys.bilibilias.download.NewDownloadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File

/**
 * 统一处理公平运行内存协议广播的接收器。
 *
 * 这层不区分具体厂商，只负责：
 * 1. 监听 `itgsa` 协议广播；
 * 2. 解析广播里的公共字段和额外字段；
 * 3. 按 `TRIM` / `KILL` 分流执行内存释放与现场保存；
 * 4. 在超时窗口内通过 Binder 将处理结果回给系统。
 */
class FairMemoryReceiver(
    private val applicationContext: Context,
    private val downloadManagerProvider: () -> NewDownloadManager,
) : IBinder.DeathRecipient {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val handlerThread = HandlerThread(TAG)
    private var handler: Handler? = null
    private var initialized = false
    private var remote: IBinder? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action !in FAIR_MEMORY_BROADCAST_ACTIONS) return

            // 广播处理可能包含 I/O 和业务回调，转异步避免阻塞接收线程。
            val pendingResult = goAsync()
            scope.launch {
                try {
                    handleIntent(intent)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    /**
     * 注册公平内存广播监听。
     *
     * 这里只注册协议定义的 `TRIM` 和 `KILL` 两个 action，并显式标记
     * `RECEIVER_EXPORTED`，因为这类广播来自系统外部进程而不是应用内自发广播。
     */
    fun initialize() {
        synchronized(this) {
            if (initialized) return

            handlerThread.start()
            handler = Handler(handlerThread.looper)
            val filter = IntentFilter().apply {
                addAction(ACTION_FAIR_MEMORY_TRIM)
                addAction(ACTION_FAIR_MEMORY_KILL)
            }
            ContextCompat.registerReceiver(
                applicationContext,
                receiver,
                filter,
                null,
                handler,
                ContextCompat.RECEIVER_EXPORTED,
            )
            initialized = true
        }
    }

    /**
     * 响应应用自身收到的标准内存回收信号。
     *
     * 这不是厂商公平内存协议的回调，而是把应用已有的 `onTrimMemory` 事件复用成
     * 一次轻量资源回收，尽量让两套内存治理行为保持一致。
     */
    fun trimMemory(clearTemporaryFiles: Boolean) {
        scope.launch {
            releaseRebuildableMemory(clearTemporaryFiles)
        }
    }

    /**
     * 当系统侧回调 Binder 失效时清理引用，避免后续继续向已失效对象回包。
     */
    override fun binderDied() {
        synchronized(this) {
            remote?.let {
                runCatching { it.unlinkToDeath(this, 0) }
            }
            remote = null
        }
    }

    /**
     * 统一处理一次公平内存广播。
     *
     * 处理顺序是：
     * 1. 解析 payload；
     * 2. 根据 `intent.action` / `bundle action` 判定本次是 `TRIM` 还是 `KILL`；
     * 3. 校验并绑定系统传入的 callback Binder；
     * 4. 执行对应处理逻辑并回包。
     */
    private suspend fun handleIntent(intent: Intent) {
        val payload = parsePayload(intent) ?: return
        // 优先使用广播 action，只有缺失时才回退到 bundle 中的 action 字段。
        val resolvedAction = resolveFairMemoryAction(intent.action, payload.bundleAction)
        if (resolvedAction == null) {
            Log.w(
                TAG,
                "收到未知的公平内存动作：intentAction=${intent.action}, bundleAction=${payload.bundleAction}",
            )
            return
        }
        if (intent.action != null && !payload.bundleAction.isNullOrBlank() && resolvedAction != payload.bundleAction) {
            Log.w(
                TAG,
                "公平内存动作不一致，优先使用广播 action：intentAction=${intent.action}, bundleAction=${payload.bundleAction}",
            )
        }
        Log.i(
            TAG,
            "收到公平内存广播：action=$resolvedAction, type=${payload.notifyType}, id=${payload.notifyId}, reason=${payload.reason}, pss=${payload.pss}/${payload.pssLimit}, heap=${payload.heapAlloc}/${payload.heapCapacity}",
        )

        val callback = payload.callback ?: run {
            Log.w(TAG, "广播中缺少 callback binder，无法回传处理结果")
            return
        }
        if (!checkRemote(callback)) return

        val result = when (resolvedAction) {
            ACTION_TRIM -> handleTrim()
            ACTION_KILL -> handleKill()
            else -> {
                Log.w(TAG, "解析后的公平内存动作不受支持：$resolvedAction")
                return
            }
        }
        reply(payload.notifyType, payload.notifyId, result, Bundle())
    }

    /**
     * 处理内存预警阶段。
     *
     * 这一阶段只释放可快速重建的资源，不做现场保存之类的重操作，目标是在更短时间内
     * 把内存占用压下来并尽快向系统回报成功/失败。
     */
    private suspend fun handleTrim(): Int {
        return runCatching {
            withTimeout(HANDLE_TIMEOUT_MS) {
                // TRIM 只做可快速重建的资源回收，不做重型持久化。
                releaseRebuildableMemory(clearTemporaryFiles = true)
            }
        }.fold(
            onSuccess = { RESULT_SUCCESS },
            onFailure = {
                Log.w(TAG, "处理内存预警失败", it)
                RESULT_FAILED
            },
        )
    }

    /**
     * 处理即将查杀阶段。
     *
     * 在总超时窗口内优先完成：
     * 1. 保存前台现场；
     * 2. 暂停下载等持续占资源任务；
     * 3. 清理可重建缓存。
     *
     * 任一步失败不会中断后续步骤，但最终会返回失败结果给系统。
     */
    private suspend fun handleKill(): Int {
        return runCatching {
            withTimeout(HANDLE_TIMEOUT_MS) {
                // KILL 阶段优先保现场和停下载，再收缩可重建资源。
                runCatching { withTimeout(SAVE_BACK_STACK_TIMEOUT_MS) { sendSaveBackStackChannel() } }
                    .onFailure { Log.w(TAG, "保存页面现场失败", it) }
                runCatching { withTimeout(PAUSE_DOWNLOAD_TIMEOUT_MS) { downloadManagerProvider().pauseAllTasks() } }
                    .onFailure { Log.w(TAG, "暂停下载任务失败", it) }
                releaseRebuildableMemory(clearTemporaryFiles = true)
            }
        }.fold(
            onSuccess = { RESULT_SUCCESS },
            onFailure = {
                Log.w(TAG, "处理查杀前准备失败", it)
                RESULT_FAILED
            },
        )
    }

    /**
     * 释放可重建的内存资源。
     *
     * 这里不碰用户持久化数据，只处理图片内存缓存、临时缓存目录和一次显式 GC。
     */
    private suspend fun releaseRebuildableMemory(clearTemporaryFiles: Boolean) {
        withContext(Dispatchers.IO) {
            runCatching {
                SingletonImageLoader.get(applicationContext).memoryCache?.clear()
            }.onFailure {
                Log.w(TAG, "清理图片内存缓存失败", it)
            }

            if (clearTemporaryFiles) {
                clearTemporaryCacheDirs()
            }

            Runtime.getRuntime().gc()
        }
    }

    /**
     * 清理应用外部缓存目录下可丢弃的临时文件。
     */
    private fun clearTemporaryCacheDirs() {
        val externalCacheDir = applicationContext.externalCacheDir ?: return
        // 这里只清理可丢弃缓存，避免误伤用户持久化内容。
        listOf("cover", "cc", "frameTemp")
            .map { File(externalCacheDir, it) }
            .forEach { dir ->
                if (dir.exists()) {
                    runCatching { dir.deleteRecursively() }
                        .onFailure { Log.w(TAG, "删除临时缓存目录失败：${dir.absolutePath}", it) }
                }
            }
    }

    /**
     * 从公平内存广播中提取业务所需字段。
     *
     * `common` 中放通知类型、通知 ID、原因、动作和 callback Binder；
     * `extra` 中放 pss / heap 等数值信息。
     */
    private fun parsePayload(intent: Intent): FairMemoryPayload? {
        val extras = intent.extras ?: return null
        val common = extras.getBundle(BUNDLE_KEY_COMMON) ?: return null
        val extra = extras.getBundle(BUNDLE_KEY_EXTRA) ?: Bundle()

        return FairMemoryPayload(
            notifyType = common.getInt(KEY_NOTIFY_TYPE),
            notifyId = common.getInt(KEY_NOTIFY_ID),
            reason = common.getString(KEY_REASON).orEmpty(),
            bundleAction = normalizeBundleAction(common.getString(KEY_ACTION)),
            callback = common.getBinderCompat(KEY_CALLBACK),
            pss = extra.getInt(KEY_PSS),
            pssLimit = extra.getInt(KEY_PSS_LIMIT),
            heapAlloc = extra.getInt(KEY_HEAP_ALLOC),
            heapCapacity = extra.getInt(KEY_HEAP_CAPACITY),
        )
    }

    /**
     * 保存并监听系统传入的 callback Binder。
     *
     * 如果本次广播带来的 Binder 和当前已保存的不同，会先解绑旧 Binder 的死亡监听，
     * 再切换到新的 Binder，保证回包始终发给当前这次通知对应的系统对象。
     */
    private fun checkRemote(callback: IBinder): Boolean {
        synchronized(this) {
            if (remote == callback) return true

            // 系统回调 binder 可能轮换，收到新 binder 后重新建立死亡监听。
            remote?.let {
                runCatching { it.unlinkToDeath(this, 0) }
            }
            return try {
                remote = callback
                callback.linkToDeath(this, 0)
                true
            } catch (e: RemoteException) {
                remote = null
                Log.w(TAG, "绑定 callback binder 死亡监听失败", e)
                false
            }
        }
    }

    /**
     * 将应用处理结果回传给系统侧公平内存服务。
     */
    private fun reply(notifyType: Int, notifyId: Int, result: Int, extra: Bundle) {
        val callback = synchronized(this) { remote } ?: return
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {
            data.writeInt(notifyType)
            data.writeInt(notifyId)
            data.writeInt(result)
            data.writeBundle(extra)
            callback.transact(TRANSACTION_EXCEPTION_REPLY, data, reply, IBinder.FLAG_ONEWAY)
            reply.readException()
        } catch (e: Exception) {
            Log.w(TAG, "回传公平内存处理结果失败", e)
        } finally {
            reply.recycle()
            data.recycle()
        }
    }

    private data class FairMemoryPayload(
        val notifyType: Int,
        val notifyId: Int,
        val reason: String,
        val bundleAction: String?,
        val callback: IBinder?,
        val pss: Int,
        val pssLimit: Int,
        val heapAlloc: Int,
        val heapCapacity: Int,
    )

    private fun Bundle.getBinderCompat(key: String): IBinder? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getBinder(key)
        } else {
            @Suppress("DEPRECATION")
            getParcelable(key)
        }
    }

    companion object {
        private const val TAG = "FairMemoryTrim"
        private const val ACTION_FAIR_MEMORY_TRIM = "itgsa.intent.action.TRIM"
        private const val ACTION_FAIR_MEMORY_KILL = "itgsa.intent.action.KILL"
        private val FAIR_MEMORY_BROADCAST_ACTIONS = setOf(
            ACTION_FAIR_MEMORY_TRIM,
            ACTION_FAIR_MEMORY_KILL,
        )
        private const val TRANSACTION_EXCEPTION_REPLY = IBinder.FIRST_CALL_TRANSACTION
        private const val HANDLE_TIMEOUT_MS = 2_500L
        private const val SAVE_BACK_STACK_TIMEOUT_MS = 1_200L
        private const val PAUSE_DOWNLOAD_TIMEOUT_MS = 800L

        private const val BUNDLE_KEY_COMMON = "common"
        private const val BUNDLE_KEY_EXTRA = "extra"
        private const val KEY_NOTIFY_TYPE = "notifyType"
        private const val KEY_NOTIFY_ID = "notifyId"
        private const val KEY_REASON = "reason"
        private const val KEY_ACTION = "action"
        private const val KEY_CALLBACK = "callback"
        private const val KEY_PSS = "pss"
        private const val KEY_PSS_LIMIT = "pssLimit"
        private const val KEY_HEAP_ALLOC = "heapAlloc"
        private const val KEY_HEAP_CAPACITY = "heapCapacity"

        internal const val ACTION_TRIM = "TRIM"
        internal const val ACTION_KILL = "KILL"
        private const val RESULT_SUCCESS = 0
        private const val RESULT_FAILED = 1

        /**
         * 解析本次广播的最终动作类型。
         *
         * 规则：
         * 1. `intent.action` 存在时只认它；
         * 2. `intent.action` 缺失时，才回退到 bundle 里的 `action`；
         * 3. 两边都无法识别时返回 `null`。
         */
        internal fun resolveFairMemoryAction(intentAction: String?, bundleAction: String?): String? {
            return when {
                intentAction == ACTION_FAIR_MEMORY_TRIM -> ACTION_TRIM
                intentAction == ACTION_FAIR_MEMORY_KILL -> ACTION_KILL
                !intentAction.isNullOrBlank() -> null
                else -> normalizeBundleAction(bundleAction)
            }
        }

        private fun normalizeBundleAction(bundleAction: String?): String? {
            return when (bundleAction?.trim()?.uppercase()) {
                ACTION_TRIM -> ACTION_TRIM
                ACTION_KILL -> ACTION_KILL
                else -> null
            }
        }
    }
}
