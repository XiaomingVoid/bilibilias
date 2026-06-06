package com.imcys.bilibilias.data.di

import com.imcys.bilibilias.database.di.databaseModule
import com.imcys.bilibilias.datastore.di.dataStoreModule
import com.imcys.bilibilias.network.di.netWorkModule
import org.koin.core.module.Module

fun sharedKoinModules(): List<Module> = listOf(
    databaseModule,
    dataStoreModule,
    netWorkModule,
    repositoryModule,
)
