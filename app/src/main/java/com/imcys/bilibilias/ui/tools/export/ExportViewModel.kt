package com.imcys.bilibilias.ui.tools.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.common.shizuku.ShizukuStateManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExportViewModel(
    private val shizukuStateManager: ShizukuStateManager,
) : ViewModel() {

    val shizukuState: StateFlow<ShizukuState> = shizukuStateManager.status
        .map { status ->
            when {
                !status.isInstalled -> ShizukuState.NoInstall
                !status.isRunning -> ShizukuState.NoRun
                !status.permissionGranted -> ShizukuState.NoPermission
                else -> ShizukuState.Normal
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ShizukuState.Normal
        )

    init {
        initEnv()
    }

    fun initEnv() {
        viewModelScope.launch {
            shizukuStateManager.refresh()
        }
    }

}
