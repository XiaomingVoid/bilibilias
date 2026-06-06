package com.imcys.bilibilias.shared.ui.model

import bilibilias.shared.generated.resources.Res
import bilibilias.shared.generated.resources.ic_bilibili_color
import bilibilias.shared.generated.resources.ic_netflix
import bilibilias.shared.generated.resources.ic_youtube
import org.jetbrains.compose.resources.DrawableResource

enum class DonghuaPlayPlatform(
    val searchUrl: String? = null,
    val officialUrl: String? = null,
    val iconResId: DrawableResource
) {
    Netflix(officialUrl = "https://www.netflix.com", iconResId = Res.drawable.ic_netflix),
    BiliBili(
        iconResId = Res.drawable.ic_bilibili_color
    ),
    Youtube(
        iconResId = Res.drawable.ic_youtube
    )
}