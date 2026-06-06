package com.imcys.bilibilias.database.di

import com.imcys.bilibilias.database.buildDatabase
import com.imcys.bilibilias.database.createDatabaseBuilder

actual fun provideDatabase() = buildDatabase(createDatabaseBuilder())
