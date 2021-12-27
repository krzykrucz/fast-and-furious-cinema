package com.krzykrucz.fastfurious.module.showtimes

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


data class DailySchedule(
    val day: Day,
    val shows: Set<Show>
)

data class Show(
    val showId: ShowId,
    val showTime: ShowTime,
    val title: Title,
    val runtime: Runtime,
    val price: USDPrice
)

@JvmInline
value class USDPrice private constructor(val cents: Int) {
    companion object {
        operator fun invoke(amount: String): USDPrice? =
            amount.takeIf { it.matches("(0|[1-9]\\d*)(\\.\\d{2})?".toRegex()) }
                ?.toDouble()
                ?.let { it * 100.0 }
                ?.toInt()
                ?.let(::USDPrice)
    }

    override fun toString() = "${cents.toString().dropLast(2)}.${cents.toString().takeLast(2)}"
}

@JvmInline
value class Runtime(val value: Duration)

@JvmInline
value class ShowId(val value: UUID) {
    companion object {
        val new get() = ShowId(UUID.randomUUID())
    }
}

@JvmInline
value class ShowTime(val value: LocalDateTime) {
    val day get() = value.toLocalDate().let(::Day)
    operator fun compareTo(time: LocalDateTime): Int = value.compareTo(time)
    operator fun plus(runtime: Runtime): LocalDateTime = value + runtime.value
}

@JvmInline
value class Title(val value: String)

@JvmInline
value class ScheduledShow(val show: Show)

@JvmInline
value class RescheduledShow(val show: Show)

@JvmInline
value class RepricedShow(val show: Show)

@JvmInline
value class RescheduledRepricedShow(val show: Show)

@JvmInline
value class Day(val value: LocalDate)

data class PlannedShow(
    val title: Title,
    val showTime: ShowTime,
    val runtime: Runtime,
    val price: USDPrice
)

data class ShowReschedule(
    val show: ShowId,
    val startTime: ShowTime?,
    val price: USDPrice?
)