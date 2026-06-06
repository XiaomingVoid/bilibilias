package com.imcys.bilibilias.network.utils

import io.ktor.http.encodeURLParameter
import okio.ByteString.Companion.encodeUtf8
import kotlin.random.Random


object BiliAppSigner {
    const val APP_KEY: String = "4409e2ce8ffd12b8"
    const val APP_SEC: String = "59b43e04ad6965f34319062b478f83dd"

    private val CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_0123456789".toCharArray()

    fun appSign(params: MutableMap<String, String>): String? {
        // 为请求参数进行 APP 签名
        params.put("appkey", APP_KEY)
        val sortedParams = params.toList().sortedBy { it.first }
        // 序列化参数
        val queryBuilder = StringBuilder()
        for ((key, value) in sortedParams) {
            if (queryBuilder.isNotEmpty()) {
                queryBuilder.append('&')
            }
            queryBuilder
                .append(key.encodeURLParameter())
                .append('=')
                .append(value.encodeURLParameter())
        }
        return queryBuilder.append(APP_SEC).toString().encodeUtf8().md5().hex()
    }

    private fun randomString(len: Int, rnd: Random = Random.Default): String =
        buildString(len) { repeat(len) { append(CHARS[rnd.nextInt(CHARS.size)]) } }


    val biliTvDeviceInfo by lazy {
        val deviceId = randomString(20)
        val buvid = randomString(37)
        val fingerprint = platformEpochMillis().toString() + randomString(45)

        val p = mutableMapOf<String, String>()
        p["bili_local_id"] = deviceId
        p["build"] = "102801"
        p["buvid"] = buvid
        p["channel"] = "master"
        p["device"] = "OnePlus"
        p["device_id"] = deviceId
        p["device_name"] = "OnePlus7TPro"
        p["device_platform"] = "Android10OnePlusHD1910"
        p["fingerprint"] = fingerprint
        p["guid"] = buvid
        p["local_fingerprint"] = fingerprint
        p["local_id"] = buvid
        p["mobi_app"] = "android_tv_yst"
        p["networkstate"] = "wifi"
        p["platform"] = "android"
        p["sys_ver"] = "29"
        p
    }
}
