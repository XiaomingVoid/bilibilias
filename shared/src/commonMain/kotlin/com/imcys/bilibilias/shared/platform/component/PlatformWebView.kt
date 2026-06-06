package com.imcys.bilibilias.shared.platform.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformWebView(
    modifier: Modifier,
    currentUrl: String,
    onUpdateUrl: (String) -> Unit = {},
    onProgressChanged: (Int) -> Unit = {}
)
