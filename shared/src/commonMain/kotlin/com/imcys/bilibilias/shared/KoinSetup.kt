package com.imcys.bilibilias.shared

import com.imcys.bilibilias.shared.di.platformKoinModules
import com.imcys.bilibilias.shared.di.sharedKoinModules
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform
/**
 * 启动Koin
 */
fun initKoin(): Koin {

    val existing = KoinPlatform.getKoinOrNull()
    if (existing != null) {
        return existing
    }
    return runCatching {
        startKoin {
            val modules = sharedKoinModules() + platformKoinModules()
            modules(modules)
        }.koin
    }.onSuccess {
    }.onFailure { throwable ->
        throwable.printStackTrace()
    }.getOrThrow()
}

fun stopKoin() {
    KoinPlatform.stopKoin()
}
