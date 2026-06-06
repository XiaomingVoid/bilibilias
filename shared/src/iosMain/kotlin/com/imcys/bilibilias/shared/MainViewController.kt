package com.imcys.bilibilias.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.imcys.bilibilias.shared.app.BILIBILIASAppScreen
import com.imcys.bilibilias.ui.theme.BILIBILIASTheme

fun MainViewController() = ComposeUIViewController {
    initKoin()
    BILIBILIASTheme {
        BILIBILIASAppScreen()
    }
}
