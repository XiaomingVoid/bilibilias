package com.imcys.bilibilias.network.service

import com.imcys.bilibilias.network.config.API.App.OLD_APP_FUNCTION_URL
import com.imcys.bilibilias.network.config.API.App.OLD_APP_INFO_URL
import com.imcys.bilibilias.network.config.API.App.OLD_VIDEO_DATA_POST_URL
import com.imcys.bilibilias.network.model.app.AppOldApplyRoamBean
import com.imcys.bilibilias.network.model.app.AppOldCommonBean
import com.imcys.bilibilias.network.model.app.AppOldDonateBean
import com.imcys.bilibilias.network.model.app.AppOldHomeBannerDataBean
import com.imcys.bilibilias.network.model.app.AppOldSoFreezeBean
import com.imcys.bilibilias.network.model.app.AppOldUpdateDataBean
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import kotlinx.coroutines.CancellationException

class AppAPIService(
    val httpClient: HttpClient
) {

    fun getToken() = (System.currentTimeMillis() / 1000) * 6

    /**
     * 检查视频冻结
     */
    suspend fun checkVideoSoFreeze(bvid: String, mid: Long): Result<AppOldSoFreezeBean> =
        runAppRequest {
            httpClient.submitForm(
                OLD_APP_FUNCTION_URL,
                formParameters = parameters {
                    append("Bvid", bvid)
                    append("Mid", mid.toString())
                }) {
                parameter("type", "SoFreeze")
            }.body()
        }


    /**
     * 冻结UP主全部视频
     */
    suspend fun freezeUpAllVideo(mid: Long): Result<AppOldSoFreezeBean> =
        runAppRequest {
            httpClient.submitForm(
                OLD_APP_FUNCTION_URL,
                formParameters = parameters {
                    append("mid", mid.toString())
                    append("token", getToken().toString())
                }) {
                parameter("type", "FUpAdd")
            }.body()
        }

    /**
     * 冻结UP主个别视频
     */
    suspend fun freezeUpVideo(mid: Long, bvid: String): Result<AppOldSoFreezeBean> =
        runAppRequest {
            httpClient.submitForm(
                OLD_APP_FUNCTION_URL,
                formParameters = parameters {
                    append("mid", mid.toString())
                    append("token", getToken().toString())
                    append("Bvid", bvid)
                }) {
                parameter("type", "FVideoAdd")
            }.body()
        }

    /**
     * 检测是否申请了漫游
     */
    suspend fun checkApplyRoam(mid: Long): Result<AppOldApplyRoamBean> =
        runAppRequest {
            httpClient.submitForm(OLD_APP_FUNCTION_URL, formParameters = parameters {
                append("mid", mid.toString())
                append("token", getToken().toString())
            }) {
                parameter("type", "CheckRoamApply")
            }.body()
        }

    /**
     * 申请漫游
     */
    suspend fun applyRoam(mid: Long, reason: String): Result<AppOldApplyRoamBean> =
        runAppRequest {
            httpClient.submitForm(OLD_APP_FUNCTION_URL, formParameters = parameters {
                append("mid", mid.toString())
                append("reason", reason)
                append("token", getToken().toString())
            }) {
                parameter("type", "CreateRoamApply")
            }.body()
        }

    /**
     * 请求捐款二维码
     */
    suspend fun getAppOldDonate(): Result<AppOldDonateBean> = runAppRequest {
        httpClient.get(OLD_APP_FUNCTION_URL) {
            parameter("type", "Donate")
        }.body()
    }


    /**
     * 请求首页banner
     */
    suspend fun getAppOldBanner(): Result<AppOldHomeBannerDataBean> = runAppRequest {
        httpClient.get(OLD_APP_INFO_URL) {
            parameter("type", "banner")
        }.body()
    }

    suspend fun getAppOldUpdateInfo(version: String): Result<AppOldUpdateDataBean> = runAppRequest {
        httpClient.get(OLD_APP_INFO_URL) {
            parameter("type", "json")
            parameter("version", version)
        }.body()
    }


    suspend fun submitASDownloadData(
        aid: Long,
        bvid: String,
        mid: Long,
        upName: String,
        tName: String,
        copy: Int,
        userName: String?,
        userId: Long?
    ) = runAppRequest {
        httpClient.get(OLD_VIDEO_DATA_POST_URL) {
            parameter("Aid", aid)
            parameter("Bvid", bvid)
            parameter("Mid", mid)
            parameter("Upname", upName)
            parameter("Tname", tName)
            parameter("Copyright", copy)
            userName?.let {
                parameter("UserName", it)
            }
            userId?.let {
                parameter("UserId", it)
            }

        }
    }

    suspend fun getAppOldBoostVideoInfo() = runAppRequest {
        httpClient.get(OLD_APP_FUNCTION_URL) {
            parameter("type", "BoostVideo")
        }.body<AppOldCommonBean>()
    }

    private suspend fun <T> runAppRequest(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
