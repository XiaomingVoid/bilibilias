package com.imcys.bilibilias

import android.app.Application
import androidx.appfunctions.AppFunctionManagerCompat
import androidx.appfunctions.service.AppFunctionConfiguration
import com.baidu.mobstat.StatService
import com.imcys.bilibilias.agent.functions.BILIAnalysisAppFunctions
import com.imcys.bilibilias.common.data.CommonBuildConfig
import com.imcys.bilibilias.common.utils.StorageUtil.getKoin
import com.imcys.bilibilias.common.utils.baiduAnalyticsSafe
import com.imcys.bilibilias.data.di.repositoryModule
import com.imcys.bilibilias.database.di.databaseModule
import com.imcys.bilibilias.datastore.di.dataStoreModule
import com.imcys.bilibilias.di.appModule
import com.imcys.bilibilias.network.di.netWorkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BILIBILIASApplication : Application(), AppFunctionConfiguration.Provider {

    override fun onCreate() {
        super.onCreate()
        // 全局异常捕获
        // AppCrashHandler.instance.init(this)
        initBuildConfig()
        // 初始化百度统计
        baiduAnalyticsSafe {
            StatService.init(this, BuildConfig.BAIDU_STAT_ID, getString(R.string.app_channel))
        }
        // Koin依赖注入
        startKoin {
            androidContext(this@BILIBILIASApplication)
            modules(
                dataStoreModule,
                netWorkModule,
                repositoryModule,
                databaseModule,
                appModule,
            )
        }
    }

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() = AppFunctionConfiguration
            .Builder()
            .addEnclosingClassFactory(BILIAnalysisAppFunctions::class.java) {
                getKoin().get<BILIAnalysisAppFunctions>()
            }
            .build()

    private fun initBuildConfig() {
        CommonBuildConfig.enabledAnalytics = BuildConfig.ENABLED_ANALYTICS
    }
}