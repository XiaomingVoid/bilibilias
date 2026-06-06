package com.imcys.bilibilias.network.logging

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

internal fun formatKtorLogMessage(
    message: String,
    json: Json,
): String {
    return try {
        val objectStart = message.indexOf('{')
        val objectEnd = message.lastIndexOf('}')
        val arrayStart = message.indexOf('[')
        val arrayEnd = message.lastIndexOf(']')

        val (bodyStart, bodyEnd) = when {
            objectStart != -1 && objectEnd > objectStart -> objectStart to objectEnd + 1
            arrayStart != -1 && arrayEnd > arrayStart -> arrayStart to arrayEnd + 1
            else -> return message
        }

        if (message.contains("application/octet-stream")) {
            return message
        }

        val prefix = message.substring(0, bodyStart)
        val body = message.substring(bodyStart, bodyEnd)
        val suffix = message.substring(bodyEnd)
        val element = json.parseToJsonElement(body)
        val formattedJson = json.encodeToString(JsonElement.serializer(), element)

        buildString {
            append(prefix.trimEnd())
            append('\n')
            append(formattedJson)
            if (suffix.isNotBlank()) {
                append('\n')
                append(suffix.trimStart())
            } else {
                append(suffix)
            }
        }
    } catch (_: Exception) {
        message
    }
}
