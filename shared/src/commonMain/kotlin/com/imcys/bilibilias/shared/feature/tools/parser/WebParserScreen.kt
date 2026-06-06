package com.imcys.bilibilias.shared.feature.tools.parser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.imcys.bilibilias.common.event.AnalysisEvent
import com.imcys.bilibilias.common.event.sendAnalysisEvent
import com.imcys.bilibilias.shared.platform.component.PlatformWebView
import com.imcys.bilibilias.ui.component.ASIconButton
import com.imcys.bilibilias.ui.component.ASTopAppBar
import com.imcys.bilibilias.ui.component.AsBackIconButton
import com.imcys.bilibilias.ui.component.BILIBILIASTopAppBarStyle
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object WebParserRoute : NavKey

@Composable
fun WebParserScreen(webParserRoute: WebParserRoute, onToBack: () -> Unit) {
    val vm = koinViewModel<WebParserViewModel>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    WebParserScaffold(onToBack, onToAs = {
        sendAnalysisEvent(AnalysisEvent(uiState.currentUrl))
    }) {
        WebParserContent(it, uiState.currentUrl, onUpdateUrl = { url ->
            vm.updateCurrentUrl(url)
        })
    }
}

@Composable
fun WebParserContent(
    paddingValues: PaddingValues,
    currentUrl: String,
    onUpdateUrl: (String) -> Unit = {}
) {
    var loadProgress by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        PlatformWebView(
            modifier = Modifier.fillMaxSize(),
            currentUrl = currentUrl,
            onUpdateUrl = onUpdateUrl,
            onProgressChanged = { loadProgress = it }
        )

        if (loadProgress in 1..99) {
            LinearProgressIndicator(
                progress = { loadProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.TopStart)
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebParserScaffold(
    onToBack: () -> Unit,
    onToAs: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            Column {
                ASTopAppBar(
                    style = BILIBILIASTopAppBarStyle.Small,
                    title = {
                        Text(text = "网页解析")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    navigationIcon = {
                        AsBackIconButton(onClick = {
                            onToBack.invoke()
                        })
                    },
                    actions = {
                        ASIconButton(onClick = {
                            onToAs()
                        }) {
                            Icon(Icons.Outlined.Check, contentDescription = "解析当前页面")
                        }
                    }
                )
            }
        },
    ) {
        content.invoke(it)
    }

}
