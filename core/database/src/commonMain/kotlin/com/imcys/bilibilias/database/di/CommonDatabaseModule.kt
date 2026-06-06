package com.imcys.bilibilias.database.di

import com.imcys.bilibilias.database.BILIBILIASDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun provideDatabase(): BILIBILIASDatabase

val databaseModule: Module = module {
    single<BILIBILIASDatabase> {
        provideDatabase()
    }
    factory {
        get<BILIBILIASDatabase>().biliUsersDao()
    }
    factory {
        get<BILIBILIASDatabase>().biliUserCookiesDao()
    }
    factory {
        get<BILIBILIASDatabase>().downloadTaskDao()
    }
}
