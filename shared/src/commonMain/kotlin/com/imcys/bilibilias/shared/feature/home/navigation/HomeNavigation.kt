package com.imcys.bilibilias.shared.feature.home.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class HomeRoute(
    val isFormLogin: Boolean = false
): NavKey