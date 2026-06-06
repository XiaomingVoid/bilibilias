package com.imcys.bilibilias.shared.feature.setting.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.shared.util.StorageInfoData
import com.imcys.bilibilias.data.repository.AppSettingsRepository
import com.imcys.bilibilias.shared.platform.storage.StoragePlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StorageManagementViewModel(
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    sealed interface StorageManagementUIState {
        object Loading : StorageManagementUIState
        data class Success(
            val storageInfoData: StorageInfoData,
            val hasDownloadSAFPermission: Boolean
        ) : StorageManagementUIState

        data class Error(val errorMsg: String) : StorageManagementUIState
    }

    private val _uiState =
        MutableStateFlow<StorageManagementUIState>(StorageManagementUIState.Loading)

    val uiState = _uiState.asStateFlow()

    fun loadStorageInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val storageInfoData = StoragePlatform.getStorageInfoData()
                val hasDownloadSAFPermission = StoragePlatform.hasDownloadSAFPermission()
                _uiState.emit(
                    StorageManagementUIState.Success(
                        storageInfoData,
                        hasDownloadSAFPermission
                    )
                )
            } catch (e: Exception) {
                _uiState.emit(StorageManagementUIState.Error(e.message ?: "未知错误"))
            }
        }
    }

    fun cleanAppCache() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_uiState.value is StorageManagementUIState.Success) {
                StoragePlatform.clearCache()
                val storageInfoData = StoragePlatform.getStorageInfoData()
                val hasDownloadSAFPermission = StoragePlatform.hasDownloadSAFPermission()
                _uiState.value =
                    StorageManagementUIState.Success(storageInfoData, hasDownloadSAFPermission)
            }
        }
    }

    fun saveDownloadUri(uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsRepository.saveDownloadSAFUriString(uri)
            loadStorageInfo()
        }
    }


}
