package com.imcys.bilibilias.ui.tools.export

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.imcys.bilibilias.R
import com.imcys.bilibilias.ui.weight.ASTopAppBar
import com.imcys.bilibilias.ui.weight.AsBackIconButton
import com.imcys.bilibilias.ui.weight.BILIBILIASTopAppBarStyle
import com.imcys.bilibilias.ui.weight.tip.ASInfoTip
import com.imcys.bilibilias.ui.weight.tip.ASWarringTip
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object ExportRoute : NavKey


@Composable
fun ExportScreen(exportRoute: ExportRoute, onToBack: () -> Unit) {
    ExportScaffold(onToBack = onToBack) {
        ExportAllPage(Modifier.padding(it))
    }
}

@Composable
fun ExportAllPage(modifier: Modifier) {
    val vm = koinViewModel<ExportViewModel>()
    val shizukuState by vm.shizukuState.collectAsStateWithLifecycle()
    Column(modifier = modifier.fillMaxSize()) {
        if (shizukuState == ShizukuState.Normal) {
            // 进入下一级
        } else {
            ShizukuStatePage(shizukuState)
        }
    }
}

@Composable
fun ShizukuStatePage(state: ShizukuState) {
    val content = rememberShizukuStateContent(state)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = CardDefaults.shape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_shizuku_logo_24),
                    contentDescription = "shizuku图标",
                    modifier = Modifier
                        .size(92.dp)
                        .aspectRatio(1f),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(18.dp))
                ShizukuStateBadge(
                    text = content.badge,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = content.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {},
                    shape = CardDefaults.shape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(content.primaryAction)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        ASWarringTip {
            Text(
                text = content.tipTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = content.tipDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(10.dp))
        ASInfoTip {
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}


@Composable
private fun ShizukuStateBadge(
    text: String,
) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                shape = CircleShape
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class ShizukuStateContent(
    val icon: ImageVector,
    val badge: String,
    val title: String,
    val description: String,
    val primaryAction: String,
    val secondaryAction: String? = null,
    val tipTitle: String,
    val tipDescription: String,
)

@Composable
private fun rememberShizukuStateContent(state: ShizukuState): ShizukuStateContent {
    return when (state) {
        ShizukuState.NoInstall -> ShizukuStateContent(
            icon = Icons.Outlined.Download,
            badge = "缺少运行环境",
            title = "先安装 Shizuku",
            description = "当前设备还没有可用的 Shizuku 环境，缓存导出功能暂时无法继续。",
            primaryAction = "查看安装指引",
            secondaryAction = "稍后再试",
            tipTitle = "为什么需要它？",
            tipDescription = "导出缓存时会涉及更高权限的文件访问能力，Shizuku 可以在不直接 root 的情况下提供这类系统接口能力。"
        )

        ShizukuState.NoRun -> ShizukuStateContent(
            icon = Icons.Outlined.PowerSettingsNew,
            badge = "服务未连接",
            title = "启动 Shizuku 服务",
            description = "Shizuku 已安装，但当前还没有运行或应用尚未成功连接到它。",
            primaryAction = "我已启动，重新检测",
            secondaryAction = "查看启动说明",
            tipTitle = "常见情况",
            tipDescription = "重启设备后 Shizuku 往往需要重新启动。如果你刚刚开启过，也可以回到这里再次检测连接状态。"
        )

        ShizukuState.NoPermission -> ShizukuStateContent(
            icon = Icons.Outlined.AdminPanelSettings,
            badge = "等待授权",
            title = "授予 Shizuku 权限",
            description = "服务已经可用，但当前应用还没有拿到对应授权，暂时不能执行导出操作。",
            primaryAction = "申请权限",
            secondaryAction = "重新检测状态",
            tipTitle = "授权说明",
            tipDescription = "授权只会开放当前功能所需的系统能力，不会自动赋予应用完整 root 权限。"
        )

        ShizukuState.Normal -> ShizukuStateContent(
            icon = Icons.Outlined.CheckCircle,
            badge = "状态正常",
            title = "Shizuku 已就绪",
            description = "当前环境已经满足要求，可以继续后续导出流程。",
            primaryAction = "继续",
            tipTitle = "已准备完成",
            tipDescription = "如果后续仍然提示连接异常，通常是服务被系统回收或权限状态发生了变化。"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportScaffold(
    onToBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            Column {
                ASTopAppBar(
                    style = BILIBILIASTopAppBarStyle.Small,
                    title = { Text(text = "缓存导出") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    navigationIcon = { AsBackIconButton(onClick = onToBack) }
                )
            }
        },
    ) {
        content.invoke(it)
    }
}
