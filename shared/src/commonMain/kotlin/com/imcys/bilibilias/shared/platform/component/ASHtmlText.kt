package com.imcys.bilibilias.shared.platform.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * 显示 HTML 富文本，支持 `<a>` 链接点击
 * @param html HTML 字符串
 * @param modifier 修饰符
 * @param onLinkClick 链接点击回调，返回 true 表示已处理，false 使用默认浏览器打开
 */
@Composable
expect fun ASHtmlText(
    html: String,
    modifier: Modifier = Modifier,
    onLinkClick: ((url: String) -> Unit)? = null
)