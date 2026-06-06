package com.imcys.bilibilias.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.okio.OkioSerializer
import okio.BufferedSink
import okio.BufferedSource

object GooglePlayerSerializer : OkioSerializer<GooglePlaySettings> {
    override val defaultValue: GooglePlaySettings = GooglePlaySettings.getDefaultInstance()

    override suspend fun readFrom(source: BufferedSource): GooglePlaySettings {
        try {
            return GooglePlaySettings.ADAPTER.decode(source)
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: GooglePlaySettings, sink: BufferedSink) {
        GooglePlaySettings.ADAPTER.encode(sink, t)
    }
}
