package com.imcys.bilibilias.common.shizuku

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.MainThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

data class ShizukuStatus(
    val isInstalled: Boolean = false,
    val isRunning: Boolean = false,
    val permissionGranted: Boolean = false,
) {
    val isAvailable: Boolean
        get() = isInstalled && isRunning && permissionGranted
}

class ShizukuStateManager(
    context: Context,
) {

    private val appContext = context.applicationContext

    private val _status = MutableStateFlow(snapshot())
    val status: StateFlow<ShizukuStatus> = _status.asStateFlow()

    private var started = false

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        refresh()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        refresh()
    }

    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { _, _ ->
        refresh()
    }

    @MainThread
    fun start() {
        if (started) return
        started = true
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
        refresh()
    }

    fun refresh() {
        _status.value = snapshot()
    }

    fun requestPermission(requestCode: Int) {
        if (!_status.value.isRunning) return
        Shizuku.requestPermission(requestCode)
    }

    private fun snapshot(): ShizukuStatus {
        val isInstalled = isShizukuInstalled()
        val isRunning = runCatching { Shizuku.pingBinder() }.getOrDefault(false)
        val permissionGranted = if (isRunning) {
            runCatching {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }.getOrDefault(false)
        } else {
            false
        }
        return ShizukuStatus(
            isInstalled = isInstalled,
            isRunning = isRunning,
            permissionGranted = permissionGranted,
        )
    }

    private fun isShizukuInstalled(): Boolean {
        val packageManager = appContext.packageManager
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    ShizukuProvider.MANAGER_APPLICATION_ID,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(ShizukuProvider.MANAGER_APPLICATION_ID, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
