package com.imcys.bilibilias.shared.feature.setting.version

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import bilibilias.shared.generated.resources.Res
import bilibilias.shared.generated.resources.version_copy_all
import com.imcys.bilibilias.common.event.sendToastEventOnBlocking
import com.imcys.bilibilias.shared.platform.runtime.getAppVersion
import com.imcys.bilibilias.shared.platform.device.getDeviceInfo
import com.imcys.bilibilias.shared.platform.device.getDeviceInfoCopyString
import com.imcys.bilibilias.shared.platform.clipboard.setClipboardText
import com.imcys.bilibilias.ui.component.ASTopAppBar
import com.imcys.bilibilias.ui.component.AsBackIconButton
import com.imcys.bilibilias.ui.component.BILIBILIASTopAppBarStyle
import com.imcys.bilibilias.ui.component.maybeNestedScroll
import io.ktor.util.collections.getValue
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data object AppVersionInfoRoute : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppVersionInfoScreen(
    appVersionInfoRoute: AppVersionInfoRoute,
    onToBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    VersionInfoScaffold(
        scrollBehavior = scrollBehavior,
        onToBack = onToBack
    ) { paddingValues ->
        VersionInfoContent(
            Modifier
                .padding(paddingValues)
                .maybeNestedScroll(scrollBehavior)
        )
    }
}

@Composable
fun VersionInfoContent(modifier: Modifier = Modifier) {
    val deviceInfo by remember {
        mutableStateOf(getDeviceInfo())
    }
    val copyText by remember {
        mutableStateOf(getDeviceInfoCopyString())
    }
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        InfoRow(label = "APP版本", value = deviceInfo.appVersion)
        InfoRow(label = "系统版本", value = deviceInfo.systemVersion)
        InfoRow(label = "设备型号", value = deviceInfo.model)
        InfoRow(label = "市场型号", value = deviceInfo.marketModel)
        InfoRow(label = "厂商", value = deviceInfo.manufacturer)
        InfoRow(label = "品牌", value = deviceInfo.brandName)
        InfoRow(label = "厂商系统名称", value = deviceInfo.osName)
        InfoRow(label = "厂商系统版本名称", value = deviceInfo.osVersionName)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                setClipboardText(copyText)
                sendToastEventOnBlocking("已复制到剪贴板")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.version_copy_all))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label：",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VersionInfoScaffold(
    scrollBehavior: TopAppBarScrollBehavior,
    onToBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            ASTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior,
                style = BILIBILIASTopAppBarStyle.Large,
                title = { Text(text = "版本信息") },
                navigationIcon = {
                    AsBackIconButton(onClick = {
                        onToBack.invoke()
                    })
                },
                alwaysDisplay = false
            )
        },
    ) {
        content(it)
    }
}
