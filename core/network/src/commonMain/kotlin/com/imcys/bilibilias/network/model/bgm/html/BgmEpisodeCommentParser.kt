package com.imcys.bilibilias.network.model.bgm.html

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element

fun Document.parseBgmEpisodeCommentPage(episodeId: Long): BgmEpisodeCommentPage {
    val subjectId = selectFirst("#headerSubject h1 a")?.attr("href")
        ?.substringAfter("/subject/", "")
        ?.substringBefore("/")
        ?.toLongOrNull()
    val commentCount = selectFirst(".singleCommentList h2.subtitle span.tip")
        ?.text()
        ?.toIntOrNull()

    val comments = select("#comment_list > div.row.row_reply")
        .map { it.toBgmEpisodeComment() }

    return BgmEpisodeCommentPage(
        episodeId = episodeId,
        subjectId = subjectId,
        commentCount = commentCount,
        comments = comments
    )
}

private fun Element.toBgmEpisodeComment(): BgmEpisodeComment {
    val timeText = selectFirst(".post_actions .action small")
        ?.text()
        .orEmpty()
        .substringAfter(" - ", "")

    val userAnchor = selectFirst(".inner > strong > a")
    val contentNode = selectFirst("div.reply_content > div.message")
    val avatarStyle = selectFirst("a.avatar span")?.attr("style")

    val replies = select("div.topic_sub_reply > div.sub_reply_bg")
        .map { it.toBgmEpisodeSubReply() }

    return BgmEpisodeComment(
        postId = id().removePrefix("post_").toLongOrNull(),
        floor = selectFirst(".floor-anchor")?.text().orEmpty(),
        timeText = timeText,
        userName = userAnchor?.text().orEmpty(),
        userPath = selectFirst("a.avatar")?.attr("href"),
        userId = attr("data-item-user").ifBlank { null },
        userSign = selectFirst(".inner > span.sign")?.text()?.trim()?.ifBlank { null },
        avatarUrl = parseBackgroundImageUrl(avatarStyle),
        contentText = contentNode?.text()?.trim().orEmpty(),
        contentHtml = contentNode?.html()?.trim().orEmpty(),
        replies = replies
    )
}

private fun Element.toBgmEpisodeSubReply(): BgmEpisodeSubReply {
    val timeText = selectFirst(".post_actions .action small")
        ?.text()
        .orEmpty()
        .substringAfter(" - ", "")

    val userAnchor = selectFirst(".inner > strong > a")
    val contentNode = selectFirst(".cmt_sub_content")
    val avatarStyle = selectFirst("a.avatar span")?.attr("style")

    return BgmEpisodeSubReply(
        postId = id().removePrefix("post_").toLongOrNull(),
        floor = selectFirst(".floor-anchor")?.text().orEmpty(),
        timeText = timeText,
        userName = userAnchor?.text().orEmpty(),
        userPath = selectFirst("a.avatar")?.attr("href"),
        userId = attr("data-item-user").ifBlank { null },
        avatarUrl = parseBackgroundImageUrl(avatarStyle),
        contentText = contentNode?.text()?.trim().orEmpty(),
        contentHtml = contentNode?.html()?.trim().orEmpty()
    )
}

private fun parseBackgroundImageUrl(style: String?): String? {
    if (style.isNullOrBlank()) return null
    val raw = URL_STYLE_REGEX.find(style)?.groupValues?.getOrNull(2) ?: return null
    return when {
        raw.startsWith("//") -> "https:$raw"
        raw.startsWith("/") -> "https://bgm.tv$raw"
        else -> raw
    }
}

private val URL_STYLE_REGEX = Regex("""url\((['"]?)(.*?)\1\)""")
