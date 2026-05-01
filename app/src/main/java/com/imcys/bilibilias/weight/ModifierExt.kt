package com.imcys.bilibilias.weight

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabPosition
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlin.math.abs
import kotlin.math.sign

context(_: TabIndicatorScope)
fun Modifier.asTabIndicatorClip(): Modifier {
    return this.clip(
        RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        )
    )
}

context(scope: TabIndicatorScope)
fun Modifier.animateIndicatorWithPager(pagerState: PagerState): Modifier {
    return with(scope) {
        tabIndicatorLayout {
                measurable: Measurable,
                constraints: Constraints,
                tabPositions: List<TabPosition>,
            ->
            if (tabPositions.isEmpty()) {
                val placeable = measurable.measure(
                    constraints.copy(
                        minWidth = 0,
                        minHeight = 0
                    )
                )
                layout(
                    constraints.maxWidth,
                    constraints.maxHeight
                ) {
                    placeable.place(0, 0)
                }
            } else {
                val currentPage =
                    pagerState.currentPage.coerceIn(tabPositions.indices)
                val targetPage =
                    (currentPage + pagerState.currentPageOffsetFraction.sign.toInt())
                        .coerceIn(tabPositions.indices)
                val fraction =
                    abs(pagerState.currentPageOffsetFraction)

                val current = tabPositions[currentPage]
                val target = tabPositions[targetPage]

                val currentWidth = current.contentWidth
                val targetWidth = target.contentWidth

                val currentStart =
                    current.left + (current.width - currentWidth) / 2f
                val targetStart =
                    target.left + (target.width - targetWidth) / 2f

                val indicatorWidth = lerp(
                    currentWidth,
                    targetWidth,
                    fraction
                ).roundToPx()
                val indicatorStart = lerp(
                    currentStart,
                    targetStart,
                    fraction
                ).roundToPx()

                val placeable =
                    measurable.measure(
                        constraints.copy(
                            minWidth = indicatorWidth,
                            maxWidth = indicatorWidth,
                        )
                    )
                layout(
                    constraints.maxWidth,
                    constraints.maxHeight
                ) {
                    placeable.place(
                        indicatorStart,
                        constraints.maxHeight - placeable.height
                    )
                }
            }
        }
    }
}


