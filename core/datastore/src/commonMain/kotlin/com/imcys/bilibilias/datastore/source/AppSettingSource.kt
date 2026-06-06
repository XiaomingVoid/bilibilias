package com.imcys.bilibilias.datastore.source

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.datastore.*
import kotlinx.coroutines.flow.first

class AppSettingSource(
    private val dataStore: DataStore<AppSettings>,
) {
    suspend fun getVideoParsePlatform() = dataStore.data.first().videoParsePlatform
}
