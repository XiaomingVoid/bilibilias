package com.imcys.bilibilias.database.di

import android.app.Application
import android.content.Context
import com.imcys.bilibilias.database.buildDatabase
import com.imcys.bilibilias.database.createDatabaseBuilder
import org.koin.mp.KoinPlatform.getKoin

actual fun provideDatabase() =
    run {
    val context: Context = getKoin().get<Application>()
    buildDatabase(createDatabaseBuilder(context))
}
