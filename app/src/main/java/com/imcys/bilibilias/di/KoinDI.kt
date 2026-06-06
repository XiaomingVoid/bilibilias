package com.imcys.bilibilias.di

import com.imcys.bilibilias.agent.functions.BILIAnalysisAppFunctions
import com.imcys.bilibilias.common.utils.firebase.FirebaseNetworkPerformanceTracer
import com.imcys.bilibilias.download.DownloadExecutor
import com.imcys.bilibilias.download.FfmpegMerger
import com.imcys.bilibilias.download.FileOutputManager
import com.imcys.bilibilias.shared.download.naming.NamingConventionHandler
import com.imcys.bilibilias.download.NewDownloadManager
import com.imcys.bilibilias.shared.download.runtime.SharedDownloadExecutor
import com.imcys.bilibilias.shared.download.runtime.SharedDownloadManager
import com.imcys.bilibilias.download.SubtitleDownloader
import com.imcys.bilibilias.download.VideoInfoFetcher
import com.imcys.bilibilias.network.config.BILIBILI_URL
import com.imcys.bilibilias.network.config.REFERER
import com.imcys.bilibilias.network.plugin.NetworkPerformanceTracer
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit


val appModule = module {
    single { androidContext().assets }
    single { androidContext().contentResolver }
    single<NetworkPerformanceTracer> {
        FirebaseNetworkPerformanceTracer()
    }
    single {
        OkHttpClient.Builder()
            .pingInterval(1, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .apply {
                        if (chain.request().headers("Referer").isEmpty()) {
                            header(REFERER, BILIBILI_URL)
                        }
                    }
                    .build()
                chain.proceed(request)
            }
            .build()
    }
    single { VideoInfoFetcher(get(), get(), get()) }
    single { FileOutputManager(androidApplication()) }
    single { FfmpegMerger(androidApplication(), get()) }
    single { NamingConventionHandler(get()) }
    single { SubtitleDownloader(get(), get(), androidApplication()) }
    single<SharedDownloadExecutor> {
        DownloadExecutor(get(qualifier = named("DownloadHttpClient")), get())
    }
    single <SharedDownloadManager>{
        NewDownloadManager(
            context = androidApplication(),
            downloadTaskRepository = get(),
            videoInfoRepository = get(),
            httpClient = get(qualifier = named("DownloadHttpClient")),
            okHttpClient = get(),
            appSettingsRepository = get(),
            videoInfoFetcher = get(),
            fileOutputManager = get(),
            downloadExecutor = get(),
            ffmpegMerger = get(),
            namingConventionHandler = get(),
            subtitleDownloader = get()
        )
    }
    factory { BILIAnalysisAppFunctions(get(), get()) }
}
