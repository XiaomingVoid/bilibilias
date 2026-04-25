package com.imcys.bilibilias.ui.tools.calendar.detail

import androidx.compose.ui.graphics.Color

sealed interface BgmCommentContentSegment {
    data class Text(
        val value: String,
        val color: Color? = null,
        val masked: Boolean = false,
        val italic: Boolean = false,
    ) : BgmCommentContentSegment

    data class Image(
        val url: String,
        val widthPx: Int? = null,
        val heightPx: Int? = null,
    ) : BgmCommentContentSegment
}

object BgmCommentContentParser {
    private val stickerRegex = Regex("\\((?:bgm\\d+|[A-Za-z]+_\\d+)\\)", RegexOption.IGNORE_CASE)
    private val imageRegex = Regex(
        pattern = "\\[img(?:=(\\d+)\\s*,\\s*(\\d+))?](.*?)\\[/img]",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )
    private val colorRegex = Regex(
        pattern = "\\[color=(#[0-9a-fA-F]{3,8})](.*?)\\[/color]",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )
    private val maskRegex = Regex(
        pattern = "\\[mask](.*?)\\[/mask]",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )
    private val italicRegex = Regex(
        pattern = "\\[i](.*?)\\[/i]",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )

    fun parse(content: String): List<BgmCommentContentSegment> {
        val cleanedContent = content
            .replace("\\r\\n", "\n")
            .replace("\\n", "\n")
            .replace("\r\n", "\n")
            .replace(stickerRegex, "")
        if (cleanedContent.isBlank()) return emptyList()

        val segments = mutableListOf<BgmCommentContentSegment>()
        var lastIndex = 0

        imageRegex.findAll(cleanedContent).forEach { match ->
            val text = cleanedContent.substring(lastIndex, match.range.first)
            val widthPx = match.groupValues.getOrNull(1)?.toIntOrNull()
            val heightPx = match.groupValues.getOrNull(2)?.toIntOrNull()
            val imageUrl = match.groupValues.getOrNull(3)?.trim().orEmpty()

            if (text.isNotBlank()) {
                segments += parseTextSegments(text)
            }
            if (imageUrl.isNotEmpty()) {
                segments += BgmCommentContentSegment.Image(
                    url = imageUrl,
                    widthPx = widthPx,
                    heightPx = heightPx
                )
            }

            lastIndex = match.range.last + 1
        }

        val tailText = cleanedContent.substring(lastIndex)
        if (tailText.isNotBlank()) {
            segments += parseTextSegments(tailText)
        }

        return segments
    }

    private fun parseTextSegments(
        text: String,
        inheritedColor: Color? = null,
        masked: Boolean = false,
        italic: Boolean = false,
    ): List<BgmCommentContentSegment.Text> {
        val segments = mutableListOf<BgmCommentContentSegment.Text>()
        val colorMatch = colorRegex.find(text)
        val maskMatch = maskRegex.find(text)
        val italicMatch = italicRegex.find(text)
        val nextMatch = listOfNotNull(colorMatch, maskMatch, italicMatch).minByOrNull { it.range.first }

        if (nextMatch == null) {
            if (text.isNotEmpty()) {
                segments += BgmCommentContentSegment.Text(
                    value = text,
                    color = inheritedColor,
                    masked = masked,
                    italic = italic
                )
            }
            return segments
        }

        val plainText = text.substring(0, nextMatch.range.first)
        if (plainText.isNotEmpty()) {
            segments += BgmCommentContentSegment.Text(
                value = plainText,
                color = inheritedColor,
                masked = masked,
                italic = italic
            )
        }

        when (nextMatch) {
            colorMatch -> {
                val color = nextMatch.groupValues.getOrNull(1)?.toCommentColor() ?: inheritedColor
                val coloredText = nextMatch.groupValues.getOrNull(2).orEmpty()
                if (coloredText.isNotEmpty()) {
                    segments += parseTextSegments(
                        text = coloredText,
                        inheritedColor = color,
                        masked = masked,
                        italic = italic
                    )
                }
            }

            maskMatch -> {
                val maskedText = nextMatch.groupValues.getOrNull(1).orEmpty()
                if (maskedText.isNotEmpty()) {
                    segments += parseTextSegments(
                        text = maskedText,
                        inheritedColor = inheritedColor,
                        masked = true,
                        italic = italic
                    )
                }
            }

            italicMatch -> {
                val italicText = nextMatch.groupValues.getOrNull(1).orEmpty()
                if (italicText.isNotEmpty()) {
                    segments += parseTextSegments(
                        text = italicText,
                        inheritedColor = inheritedColor,
                        masked = masked,
                        italic = true
                    )
                }
            }
        }

        val tailText = text.substring(nextMatch.range.last + 1)
        if (tailText.isNotEmpty()) {
            segments += parseTextSegments(
                text = tailText,
                inheritedColor = inheritedColor,
                masked = masked,
                italic = italic
            )
        }

        return segments
    }

    private fun String.toCommentColor(): Color? {
        val hex = removePrefix("#")
        val normalizedHex = when (hex.length) {
            3 -> buildString {
                append("FF")
                hex.forEach { char ->
                    append(char)
                    append(char)
                }
            }

            4 -> buildString {
                append(hex[0])
                append(hex[0])
                hex.drop(1).forEach { char ->
                    append(char)
                    append(char)
                }
            }

            6 -> "FF$hex"
            8 -> hex
            else -> return null
        }

        return runCatching {
            Color(normalizedHex.toLong(16).toInt())
        }.getOrNull()
    }
}
