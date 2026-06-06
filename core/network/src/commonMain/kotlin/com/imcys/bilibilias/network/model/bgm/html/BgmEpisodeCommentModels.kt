package com.imcys.bilibilias.network.model.bgm.html

data class BgmEpisodeCommentPage(
    val episodeId: Long,
    val subjectId: Long?,
    val commentCount: Int?,
    val comments: List<BgmEpisodeComment>
)

data class BgmEpisodeComment(
    val postId: Long?,
    val floor: String,
    val timeText: String,
    val userName: String,
    val userPath: String?,
    val userId: String?,
    val userSign: String?,
    val avatarUrl: String?,
    val contentText: String,
    val contentHtml: String,
    val replies: List<BgmEpisodeSubReply>
)

data class BgmEpisodeSubReply(
    val postId: Long?,
    val floor: String,
    val timeText: String,
    val userName: String,
    val userPath: String?,
    val userId: String?,
    val avatarUrl: String?,
    val contentText: String,
    val contentHtml: String
)
