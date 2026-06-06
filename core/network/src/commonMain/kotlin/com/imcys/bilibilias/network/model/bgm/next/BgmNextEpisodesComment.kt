package com.imcys.bilibilias.network.model.bgm.next


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BgmNextEpisodesComment(
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: Int,
    @SerialName("creatorID")
    val creatorID: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("mainID")
    val mainID: Int,
    @SerialName("relatedID")
    val relatedID: Int,
    @SerialName("replies")
    val replies: List<Reply>,
    @SerialName("state")
    val state: Int,
    @SerialName("user")
    val user: User
) {
    @Serializable
    data class Reply(
        @SerialName("content")
        val content: String,
        @SerialName("createdAt")
        val createdAt: Long,
        @SerialName("creatorID")
        val creatorID: Long,
        @SerialName("id")
        val id: Long,
        @SerialName("mainID")
        val mainID: Long,
        @SerialName("reactions")
        val reactions: List<Reaction> = emptyList(),
        @SerialName("relatedID")
        val relatedID: Long,
        @SerialName("state")
        val state: Int,
        @SerialName("user")
        val user: User
    ) {
        @Serializable
        data class Reaction(
            @SerialName("users")
            val users: List<User>,
            @SerialName("value")
            val value: Long
        ) {
            @Serializable
            data class User(
                @SerialName("id")
                val id: Long,
                @SerialName("nickname")
                val nickname: String,
                @SerialName("username")
                val username: String
            )
        }

        @Serializable
        data class User(
            @SerialName("avatar")
            val avatar: Avatar,
            @SerialName("group")
            val group: Long,
            @SerialName("id")
            val id: Long,
            @SerialName("joinedAt")
            val joinedAt: Long,
            @SerialName("nickname")
            val nickname: String,
            @SerialName("sign")
            val sign: String,
            @SerialName("username")
            val username: String
        ) {
            @Serializable
            data class Avatar(
                @SerialName("large")
                val large: String,
                @SerialName("medium")
                val medium: String,
                @SerialName("small")
                val small: String
            )
        }
    }

    @Serializable
    data class User(
        @SerialName("avatar")
        val avatar: Avatar,
        @SerialName("group")
        val group: Long,
        @SerialName("id")
        val id: Long,
        @SerialName("joinedAt")
        val joinedAt: Int,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("sign")
        val sign: String,
        @SerialName("username")
        val username: String
    ) {
        @Serializable
        data class Avatar(
            @SerialName("large")
            val large: String,
            @SerialName("medium")
            val medium: String,
            @SerialName("small")
            val small: String
        )
    }
}