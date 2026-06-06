package com.imcys.bilibilias.database.converter.download

import androidx.room3.TypeConverter
import com.imcys.bilibilias.database.entity.download.DownloadStage

class DownloadStageConverter {
    @TypeConverter
    fun fromString(value: String?): DownloadStage? {
        return value?.let { DownloadStage.valueOf(value) }
    }

    @TypeConverter
    fun stringToDownloadStage(downloadStage: DownloadStage?): String? {
        return downloadStage?.name
    }
}