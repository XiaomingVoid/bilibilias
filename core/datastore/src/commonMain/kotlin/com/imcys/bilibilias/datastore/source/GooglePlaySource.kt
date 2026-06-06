package com.imcys.bilibilias.datastore.source

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.datastore.*

class GooglePlaySource(
    private val dataStore: DataStore<GooglePlaySettings>,
) {
    val users = dataStore.data

}
