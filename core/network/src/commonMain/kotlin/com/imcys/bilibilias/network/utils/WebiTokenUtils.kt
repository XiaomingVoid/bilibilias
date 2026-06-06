package com.imcys.bilibilias.network.utils

import com.imcys.bilibilias.network.NetWorkResult
import com.imcys.bilibilias.network.config.WTS
import com.imcys.bilibilias.network.config.W_RID
import com.imcys.bilibilias.network.model.BILILoginUserInfo
import com.imcys.bilibilias.network.service.BILIBILIWebAPIService
import io.ktor.http.encodeURLParameter
import okio.ByteString.Companion.encodeUtf8

object WebiTokenUtils {
    var key: String? = null
        private set

    // 生成key的方法
    fun setKey(loginInfo: BILILoginUserInfo.WbiImg) {
        // 提取 imgKey 和 subKey
        val imgKey = loginInfo.imgUrl.replace(".png", "").split('/').last()
        val subKey = loginInfo.subUrl.replace(".png", "").split('/').last()
        val mixKey = imgKey + subKey

        // 确保 mixKey 长度足够，否则 key 生成可能失败
        key = if (mixKey.length >= (array.maxOrNull() ?: 0)) {
            array.map { mixKey[it] }.take(32).joinToString("")
        } else {
            null // 如果 mixKey 长度不足，返回null，后续逻辑可以判断是否需要重新设置key
        }
    }

    // 固定的字符索引数组
    private val array = intArrayOf(
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45,
        35, 27, 43, 5, 49, 33, 9, 42, 19, 29, 28, 14, 39, 12, 38,
        41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60,
        51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11, 36,
        20, 34, 44, 52
    )

    // 生成加密后的参数
    suspend fun BILIBILIWebAPIService.encWbi(params: Map<String, String>): Map<String, String> {
        checkToken()
        // 初始化参数并加入时间戳
        val parameters = mutableMapOf<String, String>().apply {
            put(WTS, (platformEpochMillis() / 1000).toString())
            putAll(params)
        }

        // 如果 key 为空则抛出异常
        val secretKey = key ?: throw IllegalStateException("Key is not set. Call setKey() first.")

        // 对参数进行排序，拼接字符串并加上 secretKey
        val sortedParams = parameters.entries.sortedBy { it.key }
        val dataStr = sortedParams.joinToString("&") { (k, v) ->
            k.encodeURLParameter() + "=" + v.encodeURLParameter()
        } + secretKey

        // 生成签名并加入到参数中
        parameters[W_RID] = dataStr.encodeUtf8().md5().hex()

        return parameters
    }

    private suspend fun BILIBILIWebAPIService.checkToken() {
        if (key == null) {
            updateWebiKey()
        }
    }

    /**
     * 更新Webi的Key
     */
    suspend fun BILIBILIWebAPIService.updateWebiKey() {
        println("ASShared[WebiTokenUtils] updateWebiKey() start")
        runCatching {
            getWebIInfoNoCheckLogin()
        }.onSuccess {
            println("ASShared[WebiTokenUtils] updateWebiKey() response code=${it.code}, message=${it.message}")
            it.data?.wbiImg?.let { wbiImg ->
                setKey(wbiImg)
                println("ASShared[WebiTokenUtils] key updated, isNull=${key == null}")
            } ?: println("ASShared[WebiTokenUtils] wbiImg is null")
        }.onFailure { throwable ->
            println("ASShared[WebiTokenUtils] updateWebiKey() failed: ${throwable::class.simpleName}: ${throwable.message}")
            throwable.printStackTrace()
        }
    }

}
