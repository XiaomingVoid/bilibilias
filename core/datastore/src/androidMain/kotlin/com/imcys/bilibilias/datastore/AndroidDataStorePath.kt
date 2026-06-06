package com.imcys.bilibilias.datastore

import android.content.Context
import okio.Path
import okio.Path.Companion.toPath
import org.koin.mp.KoinPlatform

internal actual fun createDataStorePath(fileName: String): Path {
    val context: Context = KoinPlatform.getKoin().get()
    return context.filesDir.resolve("datastore").resolve(fileName).absolutePath.toPath()
}
