package com.imcys.bilibilias.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.data.di.repositoryModule
import com.imcys.bilibilias.database.di.databaseModule
import com.imcys.bilibilias.datastore.di.dataStoreModule
import com.imcys.bilibilias.network.di.netWorkModule
import org.koin.compose.KoinApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.koinConfiguration


@Composable
fun ProvideKoinApplication(content: @Composable () -> Unit) {
    val context = LocalContext.current.applicationContext
    KoinApplication(configuration = koinConfiguration(declaration = {
        if (BuildConfig.DEBUG) {
            androidLogger()
        }
        androidContext(context)
        modules(
            dataStoreModule,
            netWorkModule,
            repositoryModule,
            databaseModule,
            appModule,
        )
    }), content = {
        content()
    })
}
