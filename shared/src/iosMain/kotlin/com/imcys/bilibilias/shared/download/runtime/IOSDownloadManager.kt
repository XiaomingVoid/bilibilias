package com.imcys.bilibilias.shared.download.runtime

import com.imcys.bilibilias.data.model.download.DownloadViewInfo
import com.imcys.bilibilias.data.model.video.ASLinkResultType
import com.imcys.bilibilias.shared.download.model.AppDownloadTask
import com.imcys.bilibilias.shared.platform.storage.saveImageUrlToPhotos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IOSDownloadManager : SharedDownloadManager {

    private val _downloadTasks = MutableStateFlow<List<AppDownloadTask>>(emptyList())

    override fun getAllDownloadTasks(): StateFlow<List<AppDownloadTask>> = _downloadTasks.asStateFlow()

    override suspend fun initDownloadList() {
        // 暂不实现
    }

    override suspend fun addDownloadTask(
        asLinkResultType: ASLinkResultType,
        downloadViewInfo: DownloadViewInfo
    ) {
        // 暂不实现
    }

    override suspend fun pauseTask(segmentId: Long) {
        // 暂不实现
    }

    override suspend fun resumeTask(segmentId: Long) {
        // 暂不实现
    }

    override suspend fun cancelTask(segmentId: Long) {
        // 暂不实现
    }

    override suspend fun downloadImageToAlbum(
        imageUrl: String,
        fileName: String,
        saveDirName: String
    ) {
        if (!saveImageUrlToPhotos(imageUrl)) {
            error("保存图片失败")
        }
    }
}
