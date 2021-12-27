package com.krzykrucz.fastfurious.module.showtimes

import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap


typealias PersistScheduledShow = suspend (Day, ScheduledShow) -> Unit?

val persistScheduledShow: PersistScheduledShow = { day, scheduledShow ->
    val newShow = scheduledShow.show
    dailySchedules.compute(day.value) { _, existingDailySchedule ->
        existingDailySchedule
            ?.copy(shows = existingDailySchedule.shows + newShow)
            ?: DailySchedule(day, setOf(newShow))
    }?.let {}
}

typealias PersistRescheduledShow = suspend (RescheduledRepricedShow) -> Unit?

val persistRescheduledShow: PersistRescheduledShow = { rescheduledRepricedShow ->
    val updatedShow = rescheduledRepricedShow.show
    val day = updatedShow.showTime.day
    dailySchedules.computeIfPresent(day.value) { _, existingDailySchedule ->
        val dailyShowsWithoutUpdatedShow = existingDailySchedule.shows
            .filterNot { it.showId == updatedShow.showId }
            .toSet()
        existingDailySchedule.copy(shows = dailyShowsWithoutUpdatedShow + updatedShow)
    }?.let {}
}

typealias FindDailyScheduleByShow = suspend (ShowId) -> DailySchedule?

val findDailyScheduleByShow: FindDailyScheduleByShow = { showId ->
    dailySchedules.values.find { it.shows.any { show -> show.showId == showId } }
}

typealias FindDailyScheduleByDay = suspend (Day) -> DailySchedule?

val findDailyScheduleByDay: FindDailyScheduleByDay = {
    dailySchedules[it.value]
}

val dailySchedules: MutableMap<LocalDate, DailySchedule> = ConcurrentHashMap()