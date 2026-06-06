package com.imcys.bilibilias.di

import com.imcys.bilibilias.database.di.databaseModule
import org.koin.core.module.Module

fun androidPlatformKoinModules(): List<Module> = listOf(appModule)
