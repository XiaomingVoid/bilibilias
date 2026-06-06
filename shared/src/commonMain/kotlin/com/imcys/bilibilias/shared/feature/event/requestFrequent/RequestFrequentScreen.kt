package com.imcys.bilibilias.shared.feature.event.requestFrequent


import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import bilibilias.shared.generated.resources.Res
import bilibilias.shared.generated.resources.cd_server_error_icon
import bilibilias.shared.generated.resources.common_retry
import bilibilias.shared.generated.resources.ic_cloud_alert_24px
import bilibilias.shared.generated.resources.request_frequent_continue
import bilibilias.shared.generated.resources.request_frequent_exit_app
import bilibilias.shared.generated.resources.request_frequent_incident
import bilibilias.shared.generated.resources.request_frequent_server_busy
import bilibilias.shared.generated.resources.request_frequent_success
import bilibilias.shared.generated.resources.request_frequent_testing
import bilibilias.shared.generated.resources.request_frequent_warning
import com.imcys.bilibilias.shared.platform.component.PlatformBackHandler
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
@Immutable
data class RequestFrequentRoute(
    val url: String
) : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestFrequentScreen(
    requestFrequentRoute: RequestFrequentRoute,
    onToBack: () -> Unit,
) {

    // 拦截返回
    PlatformBackHandler {}

    RequestFrequentScaffold {
        RequestFrequentContent(
            url = requestFrequentRoute.url,
            paddingValues = it,
            onToBack,
        )
    }
}

@Composable
fun RequestFrequentContent(url: String, paddingValues: PaddingValues, onToBack: () -> Unit) {

    val vm = koinViewModel<RequestFrequentViewModel>()
    val state by vm.uiState.collectAsStateWithLifecycle()

    AnimatedContent(state) {
        when (it) {
            RequestFrequentUIState.Default -> DefaultScreen(paddingValues, onRetry = {
                vm.retryRequest(url)
            })

            RequestFrequentUIState.Loading -> {
                Column(
                    Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(stringResource(Res.string.request_frequent_testing), modifier = Modifier.padding(top = 16.dp))
                }
            }

            RequestFrequentUIState.Success -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(Res.string.request_frequent_success), modifier = Modifier.padding(top = 16.dp))
                    Button(onClick = onToBack) {
                        Text(stringResource(Res.string.request_frequent_continue))
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultScreen(
    paddingValues: PaddingValues,
    onRetry: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Icon(
            painter = painterResource(Res.drawable.ic_cloud_alert_24px),
            contentDescription = stringResource(Res.string.cd_server_error_icon),
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(Res.string.request_frequent_incident),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 20.dp),
        )
        Text(
            text = stringResource(Res.string.request_frequent_server_busy),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 20.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(Res.string.request_frequent_warning),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 20.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth(),
            shape = CardDefaults.shape,
        ) {
            Text(text = stringResource(Res.string.common_retry))
        }

        // 退出APP的按钮
        OutlinedButton(
            onClick = {
                // TODO
                // killProcess(android.os.Process.myPid())
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            shape = CardDefaults.shape,
        ) {
            Text(text = stringResource(Res.string.request_frequent_exit_app))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestFrequentScaffold(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        content(it)
    }

}