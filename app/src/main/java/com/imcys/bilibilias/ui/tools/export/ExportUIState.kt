package com.imcys.bilibilias.ui.tools.export

sealed interface ExportUIState {
    data object Loading : ExportUIState
    data object Success : ExportUIState
    data object NoPermission : ExportUIState
}

sealed interface ShizukuState : ExportUIState {
    data object NoInstall : ShizukuState
    data object NoRun : ShizukuState
    data object NoPermission : ShizukuState

    data object Normal: ShizukuState
}