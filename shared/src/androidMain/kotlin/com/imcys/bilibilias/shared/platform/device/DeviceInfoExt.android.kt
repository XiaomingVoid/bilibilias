package com.imcys.bilibilias.shared.platform.device

import android.os.Build
import com.hjq.device.compat.DeviceBrand.getBrandName
import com.hjq.device.compat.DeviceMarketName
import com.hjq.device.compat.DeviceOs.getOsName
import com.hjq.device.compat.DeviceOs.getOsVersionName
import com.imcys.bilibilias.shared.platform.runtime.koinApplication

actual fun getDeviceInfoCopyString(): String {
    val deviceInfo = getDeviceInfo()
    return """
        APP版本：${deviceInfo.appVersion}
        系统版本：${deviceInfo.systemVersion}
        设备型号：${deviceInfo.model}
        市场型号：${deviceInfo.marketModel}
        厂商：${deviceInfo.manufacturer}
        品牌：${deviceInfo.brandName}
        厂商系统名称：${deviceInfo.osName}
        厂商系统版本名称：${deviceInfo.osVersionName}
    """.trimIndent()
}

actual fun getDeviceInfo(): DeviceInfo {
    val context = koinApplication
    val packageManager = context.packageManager
    val packageName = context.packageName
    val packageInfo = try {
        packageManager.getPackageInfo(packageName, 0)
    } catch (e: Exception) {
        null
    }
    val appVersion =
        "${packageInfo?.versionName}"
    val systemVersion = Build.VERSION.RELEASE ?: "未知"
    val model = Build.MODEL ?: "未知"
    val marketModel = DeviceMarketName.getMarketName(context)
    val manufacturer = Build.BRAND ?: "未知"
    val brand = Build.BRAND ?: "未知"
    val brandName = try {
        getBrandName() ?: Build.DEVICE
    } catch (_: Throwable) {
        Build.DEVICE
    }
    val osName = try {
        getOsName()
    } catch (_: Throwable) {
        "未知"
    }
    val osVersionName = try {
        getOsVersionName()
    } catch (_: Throwable) {
        "未知"
    }
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


