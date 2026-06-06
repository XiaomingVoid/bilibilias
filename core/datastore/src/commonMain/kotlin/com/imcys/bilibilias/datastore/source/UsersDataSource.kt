package com.imcys.bilibilias.datastore.source

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.datastore.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull

class UsersDataSource(
    private val dataStore: DataStore<User>,
) {
    val users = dataStore.data

    suspend fun setUserId(id: Long) {
        dataStore.updateData {
            it.copy(
                current_user_id = id,
                not_use_buvid3 = false,
            )
        }
    }

    suspend fun setNotUseBuvid3(notUse: Boolean) {
        dataStore.updateData {
            it.copy(not_use_buvid3 = notUse)
        }
    }

    suspend fun getUserId(): Long {
        return dataStore.data.first().currentUserId
    }

    suspend fun isLogin() = getUserId() != 0L

}
