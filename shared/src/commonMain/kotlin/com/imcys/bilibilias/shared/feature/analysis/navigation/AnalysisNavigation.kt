package com.imcys.bilibilias.shared.feature.analysis.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AnalysisRoute(
    val asInputText: String = ""
): NavKey
