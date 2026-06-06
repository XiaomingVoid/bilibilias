package com.imcys.bilibilias.shared.download.runtime

/**
 * 下载执行器
 * 负责执行HTTP下载，支持断点续传和重试
 */
interface SharedDownloadExecutor {
    /**
     * 下载文件到指定路径
     * @param downloadUrl 下载URL
     * @param savePath 保存路径
     * @param referer Referer头
     * @param onProgress 进度回调 (0.0 - 1.0)
     * @return 是否下载成功
     */
    suspend fun downloadFile(
        downloadUrl: String,
        savePath: String,
        referer: String,
        onProgress: (Float) -> Unit
    ): Boolean
}
