package com.imcys.bilibilias.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.TypeConverters
import com.imcys.bilibilias.database.converter.CookieEncodingConverter
import com.imcys.bilibilias.database.converter.DateConverter
import java.util.Date

@TypeConverters(CookieEncodingConverter::class, DateConverter::class)
@Entity(tableName = "bili_user_cookies")
data class BILIUserCookiesEntity(
    @PrimaryKey(true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val value: String,
    val encoding: ASSharedCookieEncoding = ASSharedCookieEncoding.URI_ENCODING,
    val domain: String? = null,
    val path: String? = null,
    val secure: Boolean = false,
    @ColumnInfo(name = "http_only")
    val httpOnly: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
)

/**
 * 需要和Ktor同步
 */
enum class ASSharedCookieEncoding {
    RAW,
    DQUOTES,
    URI_ENCODING,
    BASE64_ENCODING
}