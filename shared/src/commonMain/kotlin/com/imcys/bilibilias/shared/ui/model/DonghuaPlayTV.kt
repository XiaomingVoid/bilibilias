package com.imcys.bilibilias.shared.ui.model

import bilibilias.shared.generated.resources.Res
import bilibilias.shared.generated.resources.ic_animeism_new_logo
import bilibilias.shared.generated.resources.ic_bs11_logo
import bilibilias.shared.generated.resources.ic_bs_nihon_logo
import bilibilias.shared.generated.resources.ic_bs_tbs_logo
import bilibilias.shared.generated.resources.ic_jp_ktv_logo
import bilibilias.shared.generated.resources.ic_kbs_logo
import bilibilias.shared.generated.resources.ic_mbs_news_logo
import bilibilias.shared.generated.resources.ic_nippon_television_holdings_logo
import bilibilias.shared.generated.resources.ic_tbs_logo
import bilibilias.shared.generated.resources.ic_tnc_logo
import bilibilias.shared.generated.resources.ic_tokyo_mx_logo
import bilibilias.shared.generated.resources.ic_tv_tokyo_logo
import org.jetbrains.compose.resources.DrawableResource

data class DonghuaPlayTV(
    val name: String,
    val officialUrl: String? = null,
    val iconResId: DrawableResource? = null,
)

val playTVList = listOf(
    DonghuaPlayTV(
        name = "TOKYO MX",
        iconResId = Res.drawable.ic_tokyo_mx_logo
    ),
    DonghuaPlayTV(
        name = "BS11",
        iconResId = Res.drawable.ic_bs11_logo
    ),
    DonghuaPlayTV(
        name = "KBS京都",
        iconResId = Res.drawable.ic_kbs_logo
    ),
    DonghuaPlayTV(
        name = "BS日テレ",
        iconResId = Res.drawable.ic_bs_nihon_logo
    ),
    DonghuaPlayTV(
        name = "TBS",
        iconResId = Res.drawable.ic_tbs_logo
    ),
    DonghuaPlayTV(
        name = "MBS",
        iconResId = Res.drawable.ic_mbs_news_logo
    ),
    DonghuaPlayTV(
        name = "BS-TBS",
        iconResId = Res.drawable.ic_bs_tbs_logo
    ),
    DonghuaPlayTV(
        name = "日本テレビ系",
        iconResId = Res.drawable.ic_nippon_television_holdings_logo
    ),
    DonghuaPlayTV(
        name = "関西テレビ",
        iconResId = Res.drawable.ic_jp_ktv_logo
    ),
    DonghuaPlayTV(
        name = "テレビ西日本",
        iconResId = Res.drawable.ic_tnc_logo
    ),
    DonghuaPlayTV(
        name = "テレ東",
        iconResId = Res.drawable.ic_tv_tokyo_logo
    ),
    DonghuaPlayTV(
        name = "テレビ東京",
        iconResId = Res.drawable.ic_tv_tokyo_logo
    ),
)

val playProgramList = listOf(
    DonghuaPlayTV(
        name = "アニメイズム",
        iconResId = Res.drawable.ic_animeism_new_logo
    ),
)