package com.krzykrucz.fastfurious.module.showtimes

import arrow.core.partially1
import java.time.Clock
import java.time.LocalDateTime

typealias ScheduleShow = (PlannedShow, MovieCatalog, DailySchedule?) -> ScheduledShow?

fun scheduleShow(clock: Clock): ScheduleShow = { plannedShow, movieCatalog, maybeDailySchedule ->
    val dailySchedule = maybeDailySchedule ?: DailySchedule(plannedShow.showTime.day, emptySet())
    if (plannedShow notInThePastOnThe clock
        && plannedShow notCollidingWith dailySchedule
        && plannedShow in movieCatalog
    ) {
        Show(ShowId.new, plannedShow.showTime, plannedShow.title, plannedShow.runtime, plannedShow.price)
            .let(::ScheduledShow)
    } else null
}

typealias UpdateShow = (ShowReschedule, DailySchedule) -> RescheduledRepricedShow?

fun updateShow(clock: Clock): UpdateShow = { showReschedule, dailySchedule ->
    val findShow = findShow.partially1(showReschedule)
    val rescheduleShow = rescheduleShow.partially1(clock).partially1(dailySchedule).partially1(showReschedule)
    val repriceShow = repriceShow.partially1(showReschedule)

    val maybeScheduledShow = findShow(dailySchedule)
    val maybeRescheduledShow = maybeScheduledShow?.let(rescheduleShow)
    val maybeRepricedShow = maybeScheduledShow?.let(repriceShow)

    createResult(maybeRescheduledShow, maybeRepricedShow)
}

private typealias FindShow = (ShowReschedule, DailySchedule) -> ScheduledShow?

private val findShow: FindShow = { showReschedule, dailySchedule ->
    dailySchedule.shows.find { it.showId == showReschedule.show }
        ?.let(::ScheduledShow)
}

private typealias RescheduleShow = (Clock, DailySchedule, ShowReschedule, ScheduledShow) -> RescheduledShow?

private val rescheduleShow: RescheduleShow = { clock, dailySchedule, showReschedule, scheduledShow ->
    showReschedule.replannedFor(scheduledShow)
        .takeIf { it hasDifferentShowTimeThan scheduledShow }
        ?.takeIf { it notInThePastOnThe clock }
        ?.takeIf { it notCollidingWith (dailySchedule - scheduledShow) }
        ?.let { scheduledShow.show.copy(showTime = it.showTime) }
        ?.let(::RescheduledShow)
}

private typealias RepriceShow = (ShowReschedule, ScheduledShow) -> RepricedShow?

private val repriceShow: RepriceShow = { showReschedule, scheduledShow ->
    showReschedule.price
        ?.takeIf { newPrice -> newPrice isDifferentThanIn scheduledShow }
        ?.let { newPrice -> scheduledShow.show.copy(price = newPrice) }
        ?.let(::RepricedShow)
}

private typealias CreateResult = (RescheduledShow?, RepricedShow?) -> RescheduledRepricedShow?

private val createResult: CreateResult = { maybeRescheduledShow, maybeRepricedShow ->
    when {
        maybeRescheduledShow != null && maybeRepricedShow != null ->
            maybeRescheduledShow.show.copy(price = maybeRepricedShow.show.price)
        else ->
            maybeRescheduledShow?.show ?: maybeRepricedShow?.show
    }?.let(::RescheduledRepricedShow)
}


private operator fun DailySchedule.minus(scheduledShow: ScheduledShow): DailySchedule =
    copy(shows = shows.filterNot { it.showId == scheduledShow.show.showId }.toSet())

private fun ShowReschedule.replannedFor(scheduledShow: ScheduledShow) =
    with(scheduledShow.show) { PlannedShow(title, startTime ?: showTime, runtime, price) }

private infix fun PlannedShow.notInThePastOnThe(clock: Clock): Boolean =
    showTime > LocalDateTime.now(clock)

private infix fun PlannedShow.hasDifferentShowTimeThan(scheduledShow: ScheduledShow): Boolean =
    showTime != scheduledShow.show.showTime

private infix fun USDPrice.isDifferentThanIn(scheduledShow: ScheduledShow): Boolean =
    this != scheduledShow.show.price

private infix fun PlannedShow.notCollidingWith(dailySchedule: DailySchedule): Boolean =
    dailySchedule.shows
        .none { scheduledShow -> this overlaps scheduledShow }


private operator fun MovieCatalog.contains(plannedShow: PlannedShow): Boolean =
    movies.map(Movie::title)
        .contains(plannedShow.title)

private val PlannedShow.endTime
    get() = showTime + runtime

private val Show.endTime
    get() = showTime + runtime

private infix fun PlannedShow.overlaps(show: Show): Boolean =
    !(this.showTime > show.endTime
        || this.endTime < show.showTime.value)


