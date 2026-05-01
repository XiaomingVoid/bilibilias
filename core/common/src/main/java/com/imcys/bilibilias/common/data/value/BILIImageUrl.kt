package com.imcys.bilibilias.common.data.value

import androidx.annotation.IntRange
import kotlinx.serialization.Serializable


@Serializable
@JvmInline
value class BiliImageURL(val url: String) {


    fun toHttps(): BiliImageURL = replaceUrl { it.replace("http://", "https://") }


    /** 最大限制宽度 */
    fun width(@IntRange(from = 1) px: Int): BiliImageURL = appendParam("${px}w")

    /** 最大限制高度 */
    fun height(@IntRange(from = 1) px: Int): BiliImageURL = appendParam("${px}h")

    /** 缩放倍数 (默认100, 范围1~1000) */
    fun scale(@IntRange(from = 1, to = 1000) percent: Int): BiliImageURL = appendParam("${percent}p")

    /** 改变大小模式 */
    fun resizeMode(mode: ResizeMode): BiliImageURL = appendParam("${mode.value}e")

    fun crop(mode: CropMode = CropMode.PRESET): BiliImageURL = appendParam("${mode.value}c")

    /** 质量百分比，仅限 webp/jpeg/avif */
    fun quality(@IntRange(from = 1, to = 100) percent: Int): BiliImageURL = appendParam("${percent}q")

    fun png(): BiliImageURL = setFormat("png")
    fun jpeg(): BiliImageURL = setFormat("jpg")
    fun webp(): BiliImageURL = setFormat("webp")
    fun avif(): BiliImageURL = setFormat("avif")

    /** 获取图片平均颜色，返回 JSON: {"RGB":"#7d6f6c"} */
    fun avgColor(): BiliImageURL = setFormat("avg_color")


    /** JPEG baseline→progressive / PNG non-interlaced→interlaced */
    fun progressive(): BiliImageURL = appendParam("progressive")

    /** 来源标识 */
    fun source(tag: String): BiliImageURL {
        require(tag.isNotBlank()) { "source tag must not be blank" }
        return setSource(tag)
    }

    private fun appendParam(param: String): BiliImageURL {
        val parsed = parseUrl()
        val newParams = if (parsed.params.isEmpty()) param else "${parsed.params}_$param"
        return buildUrl(
            base = parsed.base,
            params = newParams,
            source = parsed.source,
            format = parsed.format,
            tail = parsed.tail
        )
    }

    private fun setFormat(format: String): BiliImageURL {
        val parsed = parseUrl()
        return buildUrl(
            base = parsed.base,
            params = parsed.params,
            source = parsed.source,
            format = format,
            tail = parsed.tail
        )
    }

    private fun setSource(tag: String): BiliImageURL {
        val parsed = parseUrl()
        return buildUrl(
            base = parsed.base,
            params = parsed.params,
            source = tag,
            format = parsed.format,
            tail = parsed.tail
        )
    }

    private fun parseUrl(): ParsedUrl {
        val tailStart = url.indexOfAny(charArrayOf('?', '#')).takeIf { it >= 0 } ?: url.length
        val body = url.substring(0, tailStart)
        val tail = url.substring(tailStart)

        val atIndex = body.lastIndexOf('@')
        val hasParams = atIndex > body.lastIndexOf('/')
        val base = if (hasParams) body.substring(0, atIndex) else body
        val opRaw = if (hasParams) body.substring(atIndex + 1) else ""

        val (opWithoutFormat, format) = splitTrailingFormat(opRaw)

        val sourceMark = opWithoutFormat.indexOf('!')
        val source = if (sourceMark >= 0) opWithoutFormat.substring(sourceMark + 1) else ""
        val paramsRaw = if (sourceMark >= 0) {
            opWithoutFormat.substring(0, sourceMark).removeSuffix("_")
        } else {
            opWithoutFormat
        }
        val params = normalizeParams(paramsRaw)

        return ParsedUrl(
            base = base,
            params = params,
            source = source,
            format = format,
            tail = tail
        )
    }

    private fun replaceUrl(transform: (String) -> String): BiliImageURL =
        BiliImageURL(transform(url))

    private fun buildUrl(
        base: String,
        params: String,
        source: String,
        format: String,
        tail: String
    ): BiliImageURL = buildString {
        append(base)
        if (params.isNotEmpty() || source.isNotEmpty() || format.isNotEmpty()) {
            append("@")
            if (params.isNotEmpty()) append(params)
            if (source.isNotEmpty()) {
                if (params.isNotEmpty()) append("_")
                append("!$source")
            }
            if (format.isNotEmpty()) append(".$format")
        }
        append(tail)
    }.let(::BiliImageURL)

    private fun normalizeParams(raw: String): String {
        if (raw.isEmpty()) return ""
        return raw
            .split('_')
            .filter { token ->
                token.isNotBlank() &&
                    !token.startsWith(".") &&
                    !token.contains('.') &&
                    !token.contains('!')
            }
            .joinToString("_")
    }

    private fun splitTrailingFormat(raw: String): Pair<String, String> {
        if (raw.isEmpty()) return "" to ""
        val lastDot = raw.lastIndexOf('.')
        if (lastDot < 0 || lastDot == raw.length - 1) return raw to ""
        val candidate = raw.substring(lastDot + 1)
        val valid = candidate.all { it.isLetterOrDigit() || it == '_' || it == '-' }
        if (!valid) return raw to ""
        val withoutFormat = raw.substring(0, lastDot)
        return withoutFormat to candidate
    }

    override fun toString(): String = url
}

private data class ParsedUrl(
    val base: String,
    val params: String,
    val source: String,
    val format: String,
    val tail: String
)

enum class ResizeMode(val value: Int) {
    /** 保留比例，取较小值（默认行为，避免溢出） */
    FIT_SMALLER(0),
    /** 保留比例，取较大值（可能有一边溢出容器） */
    FIT_LARGER(1),
    /** 不保留原比例，强制拉伸 */
    STRETCH(2)
}

enum class CropMode(val value: Int) {
    /** 不裁剪但会修改图片 */
    NONE(0),
    /** 上传时的预设规则，若无则右下 */
    PRESET(1),
    /** 左上 */
    TOP_LEFT(2),
    /** 右上 */
    TOP_RIGHT(3)
}
