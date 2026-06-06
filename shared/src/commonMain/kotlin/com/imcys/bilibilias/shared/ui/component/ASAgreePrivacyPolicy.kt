package com.imcys.bilibilias.shared.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imcys.bilibilias.shared.util.ASConstant.PRIVACY_POLICY_URL
import com.imcys.bilibilias.shared.platform.runtime.openLink

@Composable
fun ASAgreePrivacyPolicy(agreePrivacyPolicy: Boolean, onClick: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // 勾选同意隐私政策
        RadioButton(
            selected = agreePrivacyPolicy,
            onClick = {
                onClick()
                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
            },
            modifier = Modifier
                .padding(0.dp)
                .scale(0.75f)
                .size(20.dp)
        )
        Text("我已阅读并同意", fontSize = 14.sp)
        Text(
            text = "《BILIBILIAS 隐私政策》",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .clickable {
                    openLink(PRIVACY_POLICY_URL)
            }
        )
    }
}