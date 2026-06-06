package com.imcys.bilibilias.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.posix.gettimeofday
import platform.posix.timeval

private const val DATABASE_NAME = "bilibilias-database.db"

@OptIn(ExperimentalForeignApi::class)
fun createDatabaseBuilder(): RoomDatabase.Builder<BILIBILIASDatabase> {
    val directory = checkNotNull(
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    )

    val databasePath = requireNotNull(directory.path) + "/$DATABASE_NAME"
    return Room.databaseBuilder<BILIBILIASDatabase>(name = databasePath)
}

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long = memScoped {
    val time = alloc<timeval>()
    gettimeofday(time.ptr, null)
    (time.tv_sec * 1000L) + (time.tv_usec / 1000L)
}
