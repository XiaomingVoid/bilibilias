package com.imcys.bilibilias.shared.feature.setting

import androidx.compose.runtime.Immutable

@Immutable
data class SettingUIState(
    val isLogin: Boolean = false,
    val currentMid: Long = 0L
)
