package com.imcys.bilibilias.network.service

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.imcys.bilibilias.network.FlowNetWorkResult
import com.imcys.bilibilias.network.NetWorkResult
import com.imcys.bilibilias.network.adapter.bgmHttpRequest
import com.imcys.bilibilias.network.config.API
import com.imcys.bilibilias.network.model.BiliApiResponse
import com.imcys.bilibilias.network.model.bgm.BgmCalendar
import com.imcys.bilibilias.network.model.bgm.BgmEpisodeList
import com.imcys.bilibilias.network.model.bgm.html.BgmEpisodeCommentPage
import com.imcys.bilibilias.network.model.bgm.html.parseBgmEpisodeCommentPage
import com.imcys.bilibilias.network.model.bgm.next.BgmNextCalendar
import com.imcys.bilibilias.network.model.bgm.next.BgmNextEpisodesComment
import com.imcys.bilibilias.network.model.bgm.next.BgmNextSubject
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BgmAPIService(
    val httpClient: HttpClient
) {
    suspend fun getCalendar(): FlowNetWorkResult<List<BgmCalendar>> = httpClient.bgmHttpRequest {
        get(API.Bgm.CALENDER)
    }

    suspend fun getNextCalendar(): FlowNetWorkResult<BgmNextCalendar> = httpClient.bgmHttpRequest {
        get(API.Bgm.Next.CALENDER)
    }

    suspend fun getNextSubject(subjectId: Long): FlowNetWorkResult<BgmNextSubject> =
        httpClient.bgmHttpRequest {
            get("${API.Bgm.Next.SUBJECT_DETAIL}/$subjectId")
        }

    suspend fun getEpisodes(
        subjectId: Long,
        limit: Int = 100,
        offset: Int = 0
    ): FlowNetWorkResult<BgmEpisodeList> =
        httpClient.bgmHttpRequest {
            get(API.Bgm.GET_EPISODES) {
                parameter("subject_id", subjectId)
                parameter("limit", limit)
                parameter("offset", offset)
            }
        }

    suspend fun getEpisodesComments(
        epId: Long
    ): FlowNetWorkResult<List<BgmNextEpisodesComment>> =
        httpClient.bgmHttpRequest {
            get(API.Bgm.Next.EPISODES_COMMENTS.format(epId))
        }

    fun getEpisodeComments(epId: Long): FlowNetWorkResult<BgmEpisodeCommentPage> = flow {
        emit(NetWorkResult.Loading(true))
        try {
            val page = Ksoup.parseGetRequest(
                url = "https://bgm.tv/ep/$epId",
                httpClient = httpClient
            ) {
                headers {
                    append(HttpHeaders.Accept, "application/text")
                    append(HttpHeaders.UserAgent, BGM_USER_AGENT)
                    append(HttpHeaders.Referrer, "https://bgm.tv/")
                }
            }.parseBgmEpisodeCommentPage(episodeId = epId)

            emit(
                NetWorkResult.Success(
                    data = page,
                    responseData = BiliApiResponse(
                        code = 200,
                        message = "OK",
                        data = page,
                        result = null
                    )
                )
            )
        } catch (e: Exception) {
            emit(NetWorkResult.Error(null, null, e.message ?: ""))
        }
    }.flowOn(Dispatchers.IO)
}

private const val BGM_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"
