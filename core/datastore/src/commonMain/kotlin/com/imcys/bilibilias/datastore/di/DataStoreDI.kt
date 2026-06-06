package com.imcys.bilibilias.datastore.di

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.datastore.*
import com.imcys.bilibilias.datastore.createAppSettingsStore
import com.imcys.bilibilias.datastore.createGooglePlayStore
import com.imcys.bilibilias.datastore.createUserStore
import com.imcys.bilibilias.datastore.source.AppSettingSource
import com.imcys.bilibilias.datastore.source.GooglePlaySource
import com.imcys.bilibilias.datastore.source.UsersDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val USER_DATASTORE = "user_datastore"
private const val GOOGLE_PLAY_DATASTORE = "google_play_datastore"
private const val APP_SETTINGS_DATASTORE = "app_settings_datastore"

val dataStoreModule = module {
    single<DataStore<User>>(named(USER_DATASTORE)) {
        createUserStore()
    }
    single<DataStore<GooglePlaySettings>>(named(GOOGLE_PLAY_DATASTORE)) {
        createGooglePlayStore()
    }
    single<DataStore<AppSettings>>(named(APP_SETTINGS_DATASTORE)) {
        createAppSettingsStore()
    }
    single {
        UsersDataSource(get(named(USER_DATASTORE)))
    }
    single {
        GooglePlaySource(get(named(GOOGLE_PLAY_DATASTORE)))
    }
    single {
        AppSettingSource(get(named(APP_SETTINGS_DATASTORE)))
    }
}
