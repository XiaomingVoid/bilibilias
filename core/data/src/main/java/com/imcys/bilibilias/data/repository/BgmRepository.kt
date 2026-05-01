package com.imcys.bilibilias.data.repository

import com.imcys.bilibilias.data.model.bgm.BgmCalendarWeekData
import com.imcys.bilibilias.network.FlowNetWorkResult
import com.imcys.bilibilias.network.mapData
import com.imcys.bilibilias.network.model.bgm.BgmEpisodeList
import com.imcys.bilibilias.network.service.BgmAPIService
import kotlinx.coroutines.flow.map

class BgmRepository(
    private val bgmAPIService: BgmAPIService
) {
    suspend fun getNextCalendar(): FlowNetWorkResult<List<BgmCalendarWeekData>> {
        return bgmAPIService.getNextCalendar().map { networkResult ->
            networkResult.mapData { calendarInfo, _ ->
                val source = calendarInfo ?: return@mapData emptyList()
                listOf(
                    BgmCalendarWeekData("一", source.x1),
                    BgmCalendarWeekData("二", source.x2),
                    BgmCalendarWeekData("三", source.x3),
                    BgmCalendarWeekData("四", source.x4),
                    BgmCalendarWeekData("五", source.x5),
                    BgmCalendarWeekData("六", source.x6),
                    BgmCalendarWeekData("日", source.x7),
                )
            }
        }
    }

    suspend fun getNextSubject(subjectId: Long) = bgmAPIService.getNextSubject(subjectId)

    suspend fun getEpisodes(
        subjectId: Long,
        limit: Int = 100,
        offset: Int = 0
    ): FlowNetWorkResult<BgmEpisodeList> = bgmAPIService.getEpisodes(subjectId, limit, offset)

    fun getEpisodeComments(epId: Long) = bgmAPIService.getEpisodeComments(epId)

    suspend fun getNextEpisodesComments(
        epId: Long
    ) = bgmAPIService.getEpisodesComments(epId)
}