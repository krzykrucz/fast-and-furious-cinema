package com.krzykrucz.fastfurious.module.showtimes

import com.krzykrucz.fastfurious.monolith.EnvironmentName
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.events
import com.krzykrucz.fastfurious.monolith.publishAsync
import com.krzykrucz.fastfurious.monolith.subscribeOn
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.routing.routing
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

fun Application.showtimesModule(
    environmentName: EnvironmentName,
    configs: Map<EnvironmentName, Clock> = mapOf(EnvironmentName.LOCAL to Clock.systemUTC())
) {
    install(ContentNegotiation, ContentNegotiation.Configuration::gson)

    val clock = configs[environmentName]!!
    val viewAllUpcomingShowtimes = viewAllUpcomingShowtimes(clock)
    val scheduleShow = scheduleShow(clock)
    val updateShow = updateShow(clock)

    subscribeOn(MovieAddedToCatalogEvent) { event ->
        val movie = Movie(Title(event.title), Runtime(event.runtime))
        addMovieToCatalog(movie)
            ?: events.publishAsync(MovieAddedToCatalogEvent, event)
    }

    routing {
        route("/showtimes") {
            get("") {
                viewAllUpcomingShowtimes()
                    ?.dailyShowtimes
                    ?.let { call.respond(HttpStatusCode.OK, it) }
                    ?: call.respond(HttpStatusCode.InternalServerError)
            }
            post("/schedule") {
                val plannedShow = call.receiveOrNull<ScheduleShowRequest>()
                    ?.toPlannedShow()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val movieCatalog = getMovieCatalog()
                val day = plannedShow.showTime.day
                val dailySchedule = findDailyScheduleByDay(day)

                scheduleShow(plannedShow, movieCatalog, dailySchedule)
                    ?.takeIf { persistScheduledShow(day, it) != null }
                    ?.let { call.respond(HttpStatusCode.OK, it.show.showId.value.toString()) }
                    ?: call.respond(HttpStatusCode.InternalServerError)
            }
            put("/schedule/{showId}") {
                val showReschedule = call.receiveOrNull<RescheduleShowRequest>()
                    ?.let { body -> call.parameters["showId"]?.let { showId -> showId to body } }
                    ?.toShowReschedule()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                findDailyScheduleByShow(showReschedule.show)
                    ?.let { dailySchedule -> updateShow(showReschedule, dailySchedule) }
                    ?.let { rescheduledShow -> persistRescheduledShow(rescheduledShow) }
                    ?.let { call.respond(HttpStatusCode.OK) }
                    ?: call.respond(HttpStatusCode.InternalServerError)
            }
            post("/cancel") {
                call.respond(HttpStatusCode.NotImplemented)
            }
        }
    }
}

private data class ScheduleShowRequest(
    val title: String,
    val showTime: String,
    val runtime: String,
    val price: String
) {
    fun toPlannedShow(): PlannedShow? {
        val showTime = ShowTime(showTime) ?: return null
        val runtime = Runtime(runtime) ?: return null
        val price = USDPrice(price) ?: return null
        return PlannedShow(Title(title), showTime, runtime, price)
    }
}

private data class RescheduleShowRequest(
    val startTime: String?,
    val price: String?
)

private fun Pair<String, RescheduleShowRequest>.toShowReschedule(): ShowReschedule? {
    val showId = ShowId(first) ?: return null
    return ShowReschedule(showId, second.startTime?.let(::ShowTime), second.price?.let(USDPrice::invoke))
}


private fun ShowTime(string: String): ShowTime? =
    try {
        ShowTime(LocalDateTime.parse(string))
    } catch (error: Throwable) {
        null
    }

private fun Runtime(string: String): Runtime? =
    try {
        Runtime(Duration.parse(string))
    } catch (error: Throwable) {
        null
    }

private fun ShowId(string: String): ShowId? =
    try {
        UUID.fromString(string).let(::ShowId)
    } catch (error: Throwable) {
        null
    }