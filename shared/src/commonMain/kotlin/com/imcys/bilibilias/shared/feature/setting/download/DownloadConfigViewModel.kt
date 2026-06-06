package com.imcys.bilibilias.shared.feature.setting.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.data.repository.AppSettingsRepository
import com.imcys.bilibilias.datastore.*
import com.imcys.bilibilias.shared.platform.runtime.DownloadRuntimePlatform
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadConfigViewModel(
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {
    companion object {
        const val MAX_CONCURRENT_DOWNLOADS_WITH_SERIAL_MERGE = 10
    }

    val appSettings = appSettingsRepository.appSettingsFlow

    private val _maxSupportedConcurrentDownloads =
        MutableStateFlow(DownloadRuntimePlatform.maxSupportedConcurrentDownloads)
    val maxSupportedConcurrentDownloads = _maxSupportedConcurrentDownloads.asStateFlow()

    fun updateMaxConcurrentDownloads(value: Int) {
        viewModelScope.launch {
            val settings = appSettingsRepository.appSettingsFlow.first()
            val limit = if (settings.enabledConcurrentMerge) {
                _maxSupportedConcurrentDownloads.value
            } else {
                MAX_CONCURRENT_DOWNLOADS_WITH_SERIAL_MERGE
            }
            val downloadCount = value.coerceIn(1, limit)
            appSettingsRepository.updateMaxConcurrentDownloads(downloadCount)
            syncFfmpegConfig()
        }
    }

    fun updateEnabledConcurrentMerge(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                val settings = appSettingsRepository.appSettingsFlow.first()
                val clampedDownloadCount = settings.maxConcurrentDownloads
                    .coerceIn(1, _maxSupportedConcurrentDownloads.value)
                if (clampedDownloadCount != settings.maxConcurrentDownloads) {
                    appSettingsRepository.updateMaxConcurrentDownloads(clampedDownloadCount)
                }
            }
            appSettingsRepository.updateEnabledConcurrentMerge(enabled)
            syncFfmpegConfig()
        }
    }

    private suspend fun syncFfmpegConfig() {
        val settings = appSettingsRepository.appSettingsFlow.first()
        DownloadRuntimePlatform.applyFfmpegRuntimeConfig(
            maxConcurrentDownloads = settings.maxConcurrentDownloads,
            enabledConcurrentMerge = settings.enabledConcurrentMerge
        )
    }
}
