package com.imcys.bilibilias.shared.platform.firebase

import androidx.navigation3.runtime.NavKey

expect object FirebaseExt {
    fun setDataCollectionEnabled(boolean: Boolean)

    fun logOpenAppPage(navKey: NavKey)

    fun logRestoreBackStack(navKey: NavKey?)

    fun logLogin(method: String)

    fun logVideoParse(bvId: String?)

    fun logBangumiParse(epId: Long? = null, ssId: Long? = null)

    fun logOpenSubjectDetail(subjectId: Long)
}