package com.imcys.bilibilias.network.model.bgm

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class BgmCalendar(
    @SerialName("items")
    val items: List<Item>,
    @SerialName("weekday")
    val weekday: Weekday
) {
    @Serializable
    data class Item(
        @SerialName("air_date")
        val airDate: String,
        @SerialName("air_weekday")
        val airWeekday: Int,
        @SerialName("collection")
        val collection: Collection,
        @SerialName("id")
        val id: Int,
        @SerialName("images")
        val images: BgmSubjectImages? = null,
        @SerialName("rating")
        val rating: BgmSubjectRating? = null,
        @SerialName("name")
        val name: String,
        @SerialName("name_cn")
        val nameCn: String,
        @SerialName("rank")
        val rank: Int? = 0,
        @SerialName("summary")
        val summary: String,
        @SerialName("type")
        val type: Int,
        @SerialName("url")
        val url: String
    ) {
        @Serializable
        data class Collection(
            @SerialName("doing")
            val doing: Int
        )

    }

    @Serializable
    data class Weekday(
        @SerialName("cn")
        val cn: String,
        @SerialName("en")
        val en: String,
        @SerialName("id")
        val id: Int,
        @SerialName("ja")
        val ja: String
    )
}


@Serializable
data class BgmSubjectRating(
    @SerialName("score")
    val score: Double = 0.0,
    @SerialName("total")
    val total: Int = 0,
    val rank: Int = 0,
) {
    @Serializable
    data class Count(
        @SerialName("1")
        val x1: Int,
        @SerialName("10")
        val x10: Int,
        @SerialName("2")
        val x2: Int,
        @SerialName("3")
        val x3: Int,
        @SerialName("4")
        val x4: Int,
        @SerialName("5")
        val x5: Int,
        @SerialName("6")
        val x6: Int,
        @SerialName("7")
        val x7: Int,
        @SerialName("8")
        val x8: Int,
        @SerialName("9")
        val x9: Int
    )
}

@Serializable
data class BgmSubjectImages(
    @SerialName("common")
    val common: String,
    @SerialName("grid")
    val grid: String,
    @SerialName("large")
    val large: String,
    @SerialName("medium")
    val medium: String,
    @SerialName("small")
    val small: String
)