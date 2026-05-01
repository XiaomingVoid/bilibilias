package com.imcys.bilibilias.data.model.bgm

import com.imcys.bilibilias.network.model.bgm.next.BgmNextCalendar

data class BgmCalendarWeekData(
    val weekDayCn: String,
    val items: List<BgmNextCalendar.BgmNextCalendarDetail>
)