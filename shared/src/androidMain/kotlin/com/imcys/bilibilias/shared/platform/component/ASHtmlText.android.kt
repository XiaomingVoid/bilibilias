package com.imcys.bilibilias.shared.platform.component

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

// androidMain/ui/component/HtmlText.android.kt

import android.text.Spannable
import android.text.style.URLSpan
import android.view.MotionEvent


@Composable
actual fun ASHtmlText(
    html: String,
    modifier: Modifier,
    onLinkClick: ((url: String) -> Unit)?
) {
    val textColor = LocalContentColor.current.toArgb()

    AndroidView(
        factory = { TextView(it) },
        update = { textView ->
            textView.apply {
                setTextColor(textColor)
                text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)

                movementMethod = object : LinkMovementMethod() {
                    override fun onTouchEvent(
                        widget: TextView,
                        buffer: Spannable,
                        event: MotionEvent
                    ): Boolean {
                        if (event.action == MotionEvent.ACTION_UP) {
                            // 获取点击位置的 URLSpan
                            val x = event.x.toInt()
                            val y = event.y.toInt()
                            val line = layout.getLineForVertical(y)
                            val offset = layout.getOffsetForHorizontal(line, x.toFloat())

                            val spans = buffer.getSpans(offset, offset, URLSpan::class.java)
                            if (spans.isNotEmpty()) {
                                val url = spans[0].url
                                onLinkClick?.invoke(url)  // 把 URL 传出去
                                return true  // 拦截，不执行默认跳转
                            }
                        }
                        return super.onTouchEvent(widget, buffer, event)
                    }
                }
            }
        },
        modifier = modifier
    )
}