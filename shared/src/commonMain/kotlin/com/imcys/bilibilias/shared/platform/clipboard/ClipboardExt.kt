package com.imcys.bilibilias.shared.platform.clipboard

/**
 * 设置剪切板信息
 */
expect fun setClipboardText(text: String)

/**
 * 获取剪切板信息
 */
expect fun getClipboardText(): String?

/**
 * 读取并消费剪贴板文本（读取后清空）
 * @return 有效文本或 null
 */
expect fun readAndConsumeClipboardText(): String?
