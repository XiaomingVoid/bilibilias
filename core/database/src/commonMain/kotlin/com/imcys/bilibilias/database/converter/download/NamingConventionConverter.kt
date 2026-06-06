package com.imcys.bilibilias.database.converter.download

import androidx.room3.TypeConverter
import com.imcys.bilibilias.database.entity.download.NamingConventionInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val namingConventionJson = Json {
    ignoreUnknownKeys = true
}

class NamingConventionConverter {
    @TypeConverter
    fun fromString(value: String?): NamingConventionInfo? {
        return value?.let {
            val jsonObject = runCatching {
                namingConventionJson.parseToJsonElement(value).jsonObject
            }.getOrNull() ?: return null
            val ruleType = jsonObject["ruleType"]?.jsonPrimitive?.intOrNull
            when (ruleType) {
                NamingConventionInfo.Video().ruleType -> {
                    NamingConventionInfo.Video(
                        title = jsonObject.stringOrNull("title"),
                        pTitle = jsonObject.stringOrNull("pTitle"),
                        author = jsonObject.stringOrNull("author"),
                        bvId = jsonObject.stringOrNull("bvId"),
                        aid = jsonObject.stringOrNull("aid"),
                        cid = jsonObject.stringOrNull("cid"),
                        p = jsonObject.stringOrNull("p"),
                        collectionTitle = jsonObject.stringOrNull("collectionTitle"),
                        collectionSeasonTitle = jsonObject.stringOrNull("collectionSeasonTitle"),
                    )
                }

                NamingConventionInfo.Donghua().ruleType -> {
                    NamingConventionInfo.Donghua(
                        title = jsonObject.stringOrNull("title"),
                        episodeTitle = jsonObject.stringOrNull("episodeTitle"),
                        episodeNumber = jsonObject.stringOrNull("episodeNumber"),
                        cid = jsonObject.stringOrNull("cid"),
                        seasonTitle = jsonObject.stringOrNull("seasonTitle"),
                    )
                }

                else -> null
            }
        }
    }

    @TypeConverter
    fun stringToDownloadStage(namingConventionInfo: NamingConventionInfo?): String? {
        namingConventionInfo ?: return null

        return buildJsonObject {
            put("ruleType", JsonPrimitive(namingConventionInfo.ruleType))
            when (namingConventionInfo) {
                is NamingConventionInfo.Video -> {
                    putString("title", namingConventionInfo.title)
                    putString("pTitle", namingConventionInfo.pTitle)
                    putString("author", namingConventionInfo.author)
                    putString("bvId", namingConventionInfo.bvId)
                    putString("aid", namingConventionInfo.aid)
                    putString("cid", namingConventionInfo.cid)
                    putString("p", namingConventionInfo.p)
                    putString("collectionTitle", namingConventionInfo.collectionTitle)
                    putString("collectionSeasonTitle", namingConventionInfo.collectionSeasonTitle)
                }

                is NamingConventionInfo.Donghua -> {
                    putString("title", namingConventionInfo.title)
                    putString("episodeTitle", namingConventionInfo.episodeTitle)
                    putString("episodeNumber", namingConventionInfo.episodeNumber)
                    putString("cid", namingConventionInfo.cid)
                    putString("seasonTitle", namingConventionInfo.seasonTitle)
                }
            }
        }.toString()
    }
}

private fun JsonObject.stringOrNull(key: String): String? {
    val element = this[key] ?: return null
    return if (element is JsonNull) null else element.jsonPrimitive.content
}

private fun JsonObjectBuilder.putString(key: String, value: String?) {
    put(key, value?.let(::JsonPrimitive) ?: JsonNull)
}
