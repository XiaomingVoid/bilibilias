package com.imcys.bilibilias.ui.utils

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@Suppress("DEPRECATION")
@Composable
fun rememberWidthSizeClass(): WindowWidthSizeClass =
    currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

@Suppress("DEPRECATION")
@Composable
fun rememberHeightSizeClass(): WindowHeightSizeClass =
    currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass
