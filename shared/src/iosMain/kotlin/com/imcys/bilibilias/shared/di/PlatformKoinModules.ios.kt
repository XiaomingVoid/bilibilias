package com.imcys.bilibilias.shared.di

import com.imcys.bilibilias.shared.download.runtime.IOSDownloadExecutor
import com.imcys.bilibilias.shared.download.runtime.IOSDownloadManager
import com.imcys.bilibilias.shared.download.runtime.SharedDownloadExecutor
import com.imcys.bilibilias.shared.download.runtime.SharedDownloadManager
import com.imcys.bilibilias.network.plugin.NetworkPerformanceTracer
import org.koin.core.module.Module
import org.koin.dsl.module

private class IosNoOpNetworkPerformanceTracer : NetworkPerformanceTracer {
    override fun onRequest(
        traceNamePrefix: String,
        method: String,
        path: String,
        requestPayloadSize: Long?,
    ) = Unit

    override fun recordSuccess(
        responseCode: Int,
        responsePayloadSize: Long?,
        responseContentType: String?,
    ) = Unit

    override fun recordFailure(error: Throwable?) = Unit
}

private val iosPlatformModule = module {
    single<NetworkPerformanceTracer> {
        IosNoOpNetworkPerformanceTracer()
    }

    single< SharedDownloadExecutor> { IOSDownloadExecutor() }
    single< SharedDownloadManager> { IOSDownloadManager() }

}

internal actual fun platformKoinModules(): List<Module> = listOf(iosPlatformModule)
