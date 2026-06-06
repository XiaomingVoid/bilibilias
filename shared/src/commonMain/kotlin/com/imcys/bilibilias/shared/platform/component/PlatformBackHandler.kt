package com.imcys.bilibilias.shared.platform.component

import androidx.compose.runtime.Composable

/**
 * 跨平台返回键处理
 * - Android: 拦截物理返回键
 * - iOS: 无操作（或支持手势返回拦截）
 */
@Composable
expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)