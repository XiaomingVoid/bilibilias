package com.imcys.bilibilias.shared.platform.storage

import com.imcys.bilibilias.shared.util.StorageInfoData

expect object StoragePlatform {
    suspend fun getStorageInfoData(): StorageInfoData

    suspend fun hasDownloadSAFPermission(): Boolean

    fun clearCache(): Boolean

    fun openDownloadDirectory(): Boolean

    fun restartApplication()
}
