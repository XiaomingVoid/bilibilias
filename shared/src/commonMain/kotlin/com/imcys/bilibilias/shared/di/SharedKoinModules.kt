package com.imcys.bilibilias.shared.di

import com.imcys.bilibilias.data.di.repositoryModule
import com.imcys.bilibilias.database.di.databaseModule
import com.imcys.bilibilias.datastore.di.dataStoreModule
import com.imcys.bilibilias.network.di.netWorkModule
import com.imcys.bilibilias.shared.app.BILIBILIASAppViewModel
import com.imcys.bilibilias.shared.feature.analysis.AnalysisViewModel
import com.imcys.bilibilias.shared.feature.download.DownloadViewModel
import com.imcys.bilibilias.shared.feature.event.playvoucher.PlayVoucherErrorViewModel
import com.imcys.bilibilias.shared.feature.event.requestFrequent.RequestFrequentViewModel
import com.imcys.bilibilias.shared.feature.home.HomeViewModel
import com.imcys.bilibilias.shared.feature.login.CookieLoginViewModel
import com.imcys.bilibilias.shared.feature.login.QRCodeLoginViewModel
import com.imcys.bilibilias.shared.feature.setting.SettingViewModel
import com.imcys.bilibilias.shared.feature.setting.contract.NamingConventionViewModel
import com.imcys.bilibilias.shared.feature.setting.developer.LineConfigViewModel
import com.imcys.bilibilias.shared.feature.setting.download.DownloadConfigViewModel
import com.imcys.bilibilias.shared.feature.setting.layout.LayoutTypesetViewModel
import com.imcys.bilibilias.shared.feature.setting.platform.ParsePlatformViewModel
import com.imcys.bilibilias.shared.feature.setting.roam.RoamViewModel
import com.imcys.bilibilias.shared.feature.setting.storage.StorageManagementViewModel
import com.imcys.bilibilias.shared.feature.tools.calendar.CalendarViewModel
import com.imcys.bilibilias.shared.feature.tools.calendar.detail.SubjectDetailViewModel
import com.imcys.bilibilias.shared.feature.tools.donate.DonateViewModel
import com.imcys.bilibilias.shared.feature.tools.parser.WebParserViewModel
import com.imcys.bilibilias.shared.feature.user.UserViewModel
import com.imcys.bilibilias.shared.feature.user.bangumifollow.BangumiFollowViewModel
import com.imcys.bilibilias.shared.feature.user.folder.UserFolderViewModel
import com.imcys.bilibilias.shared.feature.user.history.UserPlayHistoryViewModel
import com.imcys.bilibilias.shared.feature.user.like.LikeVideoViewModel
import com.imcys.bilibilias.shared.feature.user.work.WorkListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val appModules = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::QRCodeLoginViewModel)
    viewModelOf(::BILIBILIASAppViewModel)
    viewModelOf(::UserViewModel)
    viewModelOf(::AnalysisViewModel)
    viewModelOf(::DownloadViewModel)
    viewModelOf(::PlayVoucherErrorViewModel)
    viewModelOf(::RoamViewModel)
    viewModelOf(::WorkListViewModel)
    viewModelOf(::BangumiFollowViewModel)
    viewModelOf(::UserFolderViewModel)
    viewModelOf(::LikeVideoViewModel)
    viewModelOf(::SettingViewModel)
    viewModelOf(::LayoutTypesetViewModel)
    viewModelOf(::UserPlayHistoryViewModel)
    viewModelOf(::CookieLoginViewModel)
    viewModelOf(::DonateViewModel)
    viewModelOf(::StorageManagementViewModel)
    viewModelOf(::NamingConventionViewModel)
    viewModelOf(::RequestFrequentViewModel)
    viewModelOf(::LineConfigViewModel)
    viewModelOf(::DownloadConfigViewModel)
    viewModelOf(::WebParserViewModel)
    viewModelOf(::ParsePlatformViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::SubjectDetailViewModel)
}

fun sharedKoinModules(): List<Module> = listOf(
    databaseModule,
    dataStoreModule,
    netWorkModule,
    repositoryModule,
    appModules
)
