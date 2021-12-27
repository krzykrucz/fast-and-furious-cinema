package com.krzykrucz.fastfurious.module.showtimes

import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class AllUpcomingShowtimes(
    val dailyShowtimes: List<DailyShowtimes>
)

data class DailyShowtimes(
    val day: ShowtimesDay,
    val showtimes: Map<MovieTitle, List<Showtime>>
)

typealias ShowtimesDay = String

typealias MovieTitle = String

typealias Showtime = String

typealias ViewAllUpcomingShowtimes = suspend () -> AllUpcomingShowtimes?

fun viewAllUpcomingShowtimes(clock: Clock): ViewAllUpcomingShowtimes = {
    try {
        val today = LocalDate.now(clock)
        dailySchedules.toMap()
            .filterKeys { day -> !day.isBefore(today) }
            .map { (_, dailySchedule) -> dailySchedule.view(clock) }
            .sortedBy { it.day }
            .let(::AllUpcomingShowtimes)
    } catch (error: Throwable) {
        log.error("Error getting view from database", error)
        null
    }
}

private val log = LoggerFactory.getLogger("ViewAllUpcomingShowtimes")

private fun DailySchedule.view(clock: Clock): DailyShowtimes =
    shows.groupBy { it.title.value }
        .mapValues { (_, shows) -> shows.view(clock) }
        .let { DailyShowtimes(day.value.toString(), it) }

private fun List<Show>.view(clock: Clock): List<Showtime> =
    map(Show::showTime)
        .map(ShowTime::value)
        .filter { time -> time.isAfter(now(clock)) }
        .sorted()
        .map(LocalDateTime::toString)