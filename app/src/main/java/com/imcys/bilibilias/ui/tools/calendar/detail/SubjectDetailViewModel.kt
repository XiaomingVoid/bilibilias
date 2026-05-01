package com.imcys.bilibilias.ui.tools.calendar.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.data.repository.BgmRepository
import com.imcys.bilibilias.network.NetWorkResult
import com.imcys.bilibilias.network.emptyNetWorkResult
import com.imcys.bilibilias.network.model.bgm.BgmEpisodeList
import com.imcys.bilibilias.network.model.bgm.next.BgmNextEpisodesComment
import com.imcys.bilibilias.network.model.bgm.next.BgmNextSubject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubjectDetailViewModel(private val bgmRepository: BgmRepository) : ViewModel() {
    private var subjectDetailJob: Job? = null
    private var episodesJob: Job? = null
    private var episodeCommentsJob: Job? = null

    val subjectData: StateFlow<NetWorkResult<BgmNextSubject>>
        field = MutableStateFlow<NetWorkResult<BgmNextSubject>>(emptyNetWorkResult())

    val episodeComments: StateFlow<NetWorkResult<List<BgmNextEpisodesComment>>>
        field = MutableStateFlow<NetWorkResult<List<BgmNextEpisodesComment>>>(emptyNetWorkResult())

    val selectedEpId: StateFlow<Long>
        field = MutableStateFlow(0L)

    val episodesData: StateFlow<NetWorkResult<BgmEpisodeList>>
        field = MutableStateFlow<NetWorkResult<BgmEpisodeList>>(emptyNetWorkResult())


    /**
     * 加载详情
     */
    fun loadSubjectDetail(subjectId: Long) {
        subjectDetailJob?.cancel()
        episodesJob?.cancel()
        episodeCommentsJob?.cancel()
        episodeComments.value = emptyNetWorkResult()
        subjectDetailJob = viewModelScope.launch {
            bgmRepository.getNextSubject(subjectId).collect {
                subjectData.value = it
            }
        }
        loadEpisodesData(subjectId)
    }

    /**
     * 加载章节信息
     */
    private fun loadEpisodesData(subjectId: Long) {
        episodesJob = viewModelScope.launch {
            bgmRepository.getEpisodes(subjectId).collect {
                episodesData.value = it
                val episodes = it.data?.data.orEmpty()
                if (episodes.isNotEmpty()) {
                    val targetEpisodeId = episodes.firstOrNull { episode ->
                        episode.id == selectedEpId.value
                    }?.id ?: episodes.first().id

                    selectedEpId.value = targetEpisodeId
                    getEpisodesComments(targetEpisodeId)
                }
            }
        }
    }

    fun selectEpisode(epId: Long) {
        if (epId == 0L || selectedEpId.value == epId) return
        selectedEpId.value = epId
        getEpisodesComments(epId)
    }

    /**
     * 加载评论列表
     */
    fun getEpisodesComments(epId: Long) {
        episodeCommentsJob?.cancel()
        episodeCommentsJob = viewModelScope.launch {
            bgmRepository.getNextEpisodesComments(epId).collect {
                episodeComments.value = it
            }
        }
    }
}
