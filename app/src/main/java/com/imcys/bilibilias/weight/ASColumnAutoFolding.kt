package com.imcys.bilibilias.weight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlin.math.abs

@Composable
fun ASColumnAutoFolding(
    modifier: Modifier = Modifier,
    onChangeShow:(Boolean)-> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            var totalScrollOffset = 0f
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < 0) {
                    totalScrollOffset += abs(delta)
                    if (totalScrollOffset > 500) {
                        onChangeShow(false)
                    }
                } else if (delta > 0) {
                    totalScrollOffset = 0f
                    onChangeShow(true)
                }
                return Offset.Zero
            }
        }
    }

    Column(modifier.nestedScroll(nestedScrollConnection)) {
        content()
    }

}