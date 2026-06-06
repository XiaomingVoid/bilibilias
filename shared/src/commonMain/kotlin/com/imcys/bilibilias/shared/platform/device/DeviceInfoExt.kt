package com.imcys.bilibilias.shared.platform.device



expect fun getDeviceInfo(): DeviceInfo

expect fun getDeviceInfoCopyString(): String


/**
 * 设备信息
 */
data class DeviceInfo(
    val appVersion: String,
    val systemVersion: String,
    val model: String,
    val marketModel: String,
    val manufacturer: String,
    val brand: String,
    val brandName: String,
    val osName: String,
    val osVersionName: String
)

