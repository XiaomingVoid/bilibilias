package com.imcys.bilibilias.network.model.bgm.next

import com.imcys.bilibilias.network.model.bgm.BgmSubjectImages
import com.imcys.bilibilias.network.model.bgm.BgmSubjectRating
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

@Serializable
data class BgmNextCalendar(
    @SerialName("1")
    val x1: List<BgmNextCalendarDetail>,
    @SerialName("2")
    val x2: List<BgmNextCalendarDetail>,
    @SerialName("3")
    val x3: List<BgmNextCalendarDetail>,
    @SerialName("4")
    val x4: List<BgmNextCalendarDetail>,
    @SerialName("5")
    val x5: List<BgmNextCalendarDetail>,
    @SerialName("6")
    val x6: List<BgmNextCalendarDetail>,
    @SerialName("7")
    val x7: List<BgmNextCalendarDetail>,
) {
    @Serializable
    data class BgmNextCalendarDetail(
        @SerialName("subject")
        val subject: BgmCalendarSubject,
        @SerialName("watchers")
        val watchers: Int
    )
}


@Serializable
data class BgmCalendarSubject(
    @SerialName("id")
    val id: Long,
    @SerialName("images")
    val images: BgmSubjectImages?,
    @SerialName("info")
    val info: String = "",
    @SerialName("locked")
    val locked: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("nameCN")
    val nameCN: String,
    @SerialName("nsfw")
    val nsfw: Boolean,
    @SerialName("rating")
    val rating: BgmSubjectRating?,
    @SerialName("type")
    val type: Int
)

