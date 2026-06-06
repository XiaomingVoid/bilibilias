package com.imcys.bilibilias.data.model.download

import com.imcys.bilibilias.database.entity.download.DownloadState
import com.imcys.bilibilias.database.entity.download.DownloadSubTaskType
import com.imcys.bilibilias.database.currentTimeMillis

data class DownloadSubTask(
    val segmentId: Long,
    val savePath: String,
    val progress: Float = 0.0f,
    val subTaskType: DownloadSubTaskType,
    val downloadState: DownloadState = DownloadState.WAITING,
    val createTime: Long = currentTimeMillis(),
    val updateTime: Long = currentTimeMillis(),
)
