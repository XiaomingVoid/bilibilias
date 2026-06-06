package com.imcys.bilibilias.shared.feature.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bilibilias.shared.generated.resources.Res
import bilibilias.shared.generated.resources.login_after_you_can
import bilibilias.shared.generated.resources.login_agreement_prefix
import bilibilias.shared.generated.resources.login_benefits
import bilibilias.shared.generated.resources.login_bilibili_agreement
import bilibilias.shared.generated.resources.login_disclaimer
import bilibilias.shared.generated.resources.login_privacy_agreement
import bilibilias.shared.generated.resources.login_scan_qrcode
import bilibilias.shared.generated.resources.login_special_notice
import bilibilias.shared.generated.resources.login_title
import com.imcys.bilibilias.shared.util.ASConstant
import com.imcys.bilibilias.shared.platform.runtime.openLink
import com.imcys.bilibilias.ui.component.ASTopAppBar
import com.imcys.bilibilias.ui.component.AsBackIconButton
import com.imcys.bilibilias.ui.component.BILIBILIASTopAppBarStyle
import org.jetbrains.compose.resources.stringResource


@Composable
internal fun LoginRoute(
    onToBack: () -> Unit,
    goToQRCodeLogin: () -> Unit,
) {
    LoginScreen(onToBack, goToQRCodeLogin)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onToBack: () -> Unit, goToQRCodeLogin: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            Column {
                ASTopAppBar(
                    style = BILIBILIASTopAppBarStyle.Small,
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(stringResource(Res.string.login_title))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    navigationIcon = {
                        AsBackIconButton { onToBack.invoke() }
                    }
                )
            }

        },

        ) {

        Box(Modifier.padding(it)) {
            Column(Modifier.padding(24.dp)) {
                LazyColumn(
                    Modifier.weight(1f).padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OutlinedCard(
                            Modifier
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Text(stringResource(Res.string.login_after_you_can), fontSize = 14.sp)
                                Text(
                                    stringResource(Res.string.login_benefits), fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        OutlinedCard(
                            Modifier
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Text(stringResource(Res.string.login_agreement_prefix), fontSize = 14.sp)
                                Text(
                                    stringResource(Res.string.login_bilibili_agreement), fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    stringResource(Res.string.login_privacy_agreement),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable {
                                        openLink(ASConstant.PRIVACY_POLICY_URL)
                                    }
                                )
                                Text(
                                    stringResource(Res.string.login_disclaimer), fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = CardDefaults.shape,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.tertiaryContainer),
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Text(
                                    stringResource(Res.string.login_special_notice),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondaryContainer),
                    shape = CardDefaults.shape,
                    onClick = { goToQRCodeLogin.invoke() }
                ) {
                    Text(
                        stringResource(Res.string.login_scan_qrcode),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp
                    )
                }
            }

        }
    }
}
