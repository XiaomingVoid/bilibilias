package com.imcys.bilibilias.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

private const val DATABASE_NAME = "bilibilias-database"

fun createDatabaseBuilder(context: Context): RoomDatabase.Builder<BILIBILIASDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<BILIBILIASDatabase>(
        context = appContext,
        name = databaseFile.absolutePath,
    )
}

actual fun currentTimeMillis(): Long = System.currentTimeMillis()
