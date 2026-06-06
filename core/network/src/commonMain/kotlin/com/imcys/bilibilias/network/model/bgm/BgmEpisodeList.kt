package com.imcys.bilibilias.network.model.bgm


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BgmEpisodeList(
    @SerialName("data")
    val `data`: List<Data>,
    @SerialName("limit")
    val limit: Int,
    @SerialName("offset")
    val offset: Int,
    @SerialName("total")
    val total: Int
) {
    @Serializable
    data class Data(
        @SerialName("airdate")
        val airdate: String,
        @SerialName("comment")
        val comment: Int,
        @SerialName("desc")
        val desc: String,
        @SerialName("disc")
        val disc: Int,
        @SerialName("duration")
        val duration: String,
        @SerialName("duration_seconds")
        val durationSeconds: Int,
        @SerialName("ep")
        val ep: Long,
        @SerialName("id")
        val id: Long,
        @SerialName("name")
        val name: String,
        @SerialName("name_cn")
        val nameCn: String,
        @SerialName("sort")
        val sort: Int,
        @SerialName("subject_id")
        val subjectId: Long,
        @SerialName("type")
        val type: Int
    )
}