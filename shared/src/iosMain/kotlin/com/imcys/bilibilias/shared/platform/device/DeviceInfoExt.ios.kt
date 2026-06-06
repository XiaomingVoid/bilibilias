package com.imcys.bilibilias.shared.platform.device

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.posix.uname
import platform.posix.utsname

// iosMain/platform/DeviceInfo.ios.kt

import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

actual fun getDeviceInfo(): DeviceInfo {
    val device = UIDevice.currentDevice
    val bundle = NSBundle.mainBundle
    val info = bundle.infoDictionary

    // APP 版本
    val versionName = info?.get("CFBundleShortVersionString") as? String ?: "1.0.0"
    val buildNumber = info?.get("CFBundleVersion") as? String ?: "1"
    val appVersion = "$versionName (release-ios-$buildNumber)"

    // 系统版本
    val systemVersion = device.systemVersion ?: "未知"

    val model = device.model ?: "iPhone"

    val marketModel = model

    val manufacturer = "Apple"
    val brand = "Apple"
    val brandName = "Apple"
    val osName = "iOS"
    val osVersionName = "iOS $systemVersion"

    return DeviceInfo(
        appVersion = appVersion,
        systemVersion = systemVersion,
        model = model,
        marketModel = marketModel,
        manufacturer = manufacturer,
        brand = brand,
        brandName = brandName,
        osName = osName,
        osVersionName = osVersionName
    )
}

actual fun getDeviceInfoCopyString(): String {
    val info = getDeviceInfo()
    return """
        APP版本：${info.appVersion}
        系统版本：${info.systemVersion}
        设备型号：${info.model}
        市场型号：${info.marketModel}
        厂商：${info.manufacturer}
        品牌：${info.brandName}
        厂商系统名称：${info.osName}
        厂商系统版本名称：${info.osVersionName}
    """.trimIndent()
}