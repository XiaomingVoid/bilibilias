package com.imcys.bilibilias.shared.feature.tools.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.data.model.bgm.BgmCalendarWeekData
import com.imcys.bilibilias.data.repository.BgmRepository
import com.imcys.bilibilias.network.NetWorkResult
import com.imcys.bilibilias.network.emptyNetWorkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CalendarViewModel(private val bgmRepository: BgmRepository) : ViewModel() {

    val calendarData: StateFlow<NetWorkResult<List<BgmCalendarWeekData>>>
        field = MutableStateFlow<NetWorkResult<List<BgmCalendarWeekData>>>(emptyNetWorkResult())

    val selectedWeekIndex: StateFlow<Int>
        field = MutableStateFlow<Int>(currentWeekIndex)


    val currentWeekIndex: Int
        get() {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return when (today.dayOfWeek.isoDayNumber) {
                7 -> 6
                else -> today.dayOfWeek.isoDayNumber - 1
            }
        }

    init {
        getCalendarInfo()
    }

    fun updateSelectedWeekIndex(index: Int) {
        selectedWeekIndex.value = index
    }

    fun getCalendarInfo() {
        viewModelScope.launch {
            bgmRepository.getNextCalendar().collect {
                calendarData.value = it
            }
        }
    }
}
