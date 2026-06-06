package com.imcys.bilibilias.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.okio.OkioSerializer
import okio.BufferedSink
import okio.BufferedSource

object UserSerializer : OkioSerializer<User> {
    override val defaultValue: User = User.getDefaultInstance()

    override suspend fun readFrom(source: BufferedSource): User {
        try {
            return User.ADAPTER.decode(source)
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: User, sink: BufferedSink) {
        User.ADAPTER.encode(sink, t)
    }
}
