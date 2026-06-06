package com.imcys.bilibilias.shared.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

fun Int.blend(
    color: Int,
    fraction: Float = 0.5f,
): Int {
    val startRed = red()
    val startGreen = green()
    val startBlue = blue()
    val endRed = color.red()
    val endGreen = color.green()
    val endBlue = color.blue()

    return (lerp(startRed, endRed, fraction) shl 16) or
            (lerp(startGreen, endGreen, fraction) shl 8) or
            lerp(startBlue, endBlue, fraction)
}

private fun Int.red(): Int = (this shr 16) and 0xff

private fun Int.green(): Int = (this shr 8) and 0xff

private fun Int.blue(): Int = this and 0xff

private fun lerp(start: Int, end: Int, fraction: Float): Int {
    return (start + ((end - start) * fraction)).roundToInt()
}


@Composable
fun rememberKonfettiState(visible: Boolean = false): MutableState<Boolean> {
    return remember { mutableStateOf(visible) }
}

@Composable
fun Konfetti(
    state: MutableState<Boolean> = rememberKonfettiState(),
    modifier: Modifier = Modifier,
    primary: Color = MaterialTheme.colorScheme.primary
) {
    var visible by state
    if (!visible) {
        return
    }
    val haptics = LocalHapticFeedback.current

    ConfettiKit(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        parties = remember { particles(primary.toArgb()) },
        onParticleSystemEnded = { _, activeSystems ->
            if (activeSystems == 0) {
                visible = false
            } else {
                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
            }
        }
    )
}

private val defaultColors = listOf(
    0xfce18a,
    0x009688,
    0xff726d,
    0xf4306d,
    0xb48def,
    0x95FF82,
    0x82ECFF,
    0xFF9800,
    0x0E008A,
)
private const val colorBlendFraction = 0.3f

private fun particles(primary: Int) = listOf(
    Party(
        speed = 0f,
        maxSpeed = 12f,
        damping = 0.9f,
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        colors = defaultColors.map { it.blend(primary, colorBlendFraction) },
        emitter = Emitter(duration = 2.seconds).perSecond(100),
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 55,
        spread = 60,
        colors = defaultColors.map { it.blend(primary, colorBlendFraction) },
        emitter = Emitter(duration = 2.seconds).perSecond(100),
        position = Position.Relative(0.0, 1.0)
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 125,
        spread = 60,
        colors = defaultColors.map { it.blend(primary, colorBlendFraction) },
        emitter = Emitter(duration = 2.seconds).perSecond(100),
        position = Position.Relative(1.0, 1.0)
    )
)
