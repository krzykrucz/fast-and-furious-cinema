package com.krzykrucz.fastfurious.module.moviedetails

import com.krzykrucz.fastfurious.monolith.IntegrationEvent
import com.krzykrucz.fastfurious.support.GivenApp
import com.krzykrucz.fastfurious.support.KtorModuleTestSupport
import io.kotest.assertions.ktor.shouldHaveContent
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.ktor.application.Application
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Month.JUNE
import java.util.UUID

class MovieDetailsSpec : BehaviorSpec({

    `Given movie details app`(" and movie is added to catalog") { app ->
        app.movieIsAddedToCatalog(
            id = "tt0463985",
            title = "The Fast and the Furious: Tokyo Drift",
            description = "description",
            releaseDate = LocalDate.of(2006, JUNE, 16),
            runtime = Duration.ofMinutes(104),
            imdbRating = 6.1
        )
        When("all details are fetched") {
            eventually {
                val response = app.handleRequest(Get, "/details").response

                Then("one movie details with no rating") {
                    // language=JSON
                    response shouldHaveContent """[{
                        |"id":"tt0463985",
                        |"title":"The Fast and the Furious: Tokyo Drift",
                        |"description":"description",
                        |"releaseDate":"2006-06-16",
                        |"rating":"0.0",
                        |"votes":"0",
                        |"imdbRating":"6.1",
                        |"runtime":"104 min"
                        |}]""".trimMargin().toSingleLine()
                }
            }
        }
    }
    `Given movie details app`(" and movie is rated") { app ->
        app.movieIsAddedToCatalog(
            id = "tt0463985",
            title = "The Fast and the Furious: Tokyo Drift",
            description = "description",
            releaseDate = LocalDate.of(2006, JUNE, 16),
            runtime = Duration.ofMinutes(104),
            imdbRating = 6.1
        )
        app.movieIsRated(
            id = "tt0463985",
            votesCount = 11,
            averageRating = 6.51
        )

        When("all details are fetched") {
            eventually {
                val response = app.handleRequest(Get, "/details").response

                Then("one movie details with rating updated") {
                    // language=JSON
                    response shouldHaveContent """[{
                        |"id":"tt0463985",
                        |"title":"The Fast and the Furious: Tokyo Drift",
                        |"description":"description",
                        |"releaseDate":"2006-06-16",
                        |"rating":"6.5",
                        |"votes":"11",
                        |"imdbRating":"6.1",
                        |"runtime":"104 min"
                        |}]""".trimMargin().toSingleLine()
                }
            }
        }
    }
    `Given movie details app`(" and movie rated event is duplicated") { app ->
        app.movieIsAddedToCatalog(
            id = "tt0463985",
            title = "The Fast and the Furious: Tokyo Drift",
            description = "description",
            releaseDate = LocalDate.of(2006, JUNE, 16),
            runtime = Duration.ofMinutes(104),
            imdbRating = 6.1
        )
        val eventId = UUID.randomUUID().toString()
        app.movieIsRated(
            eventId = eventId,
            id = "tt0463985",
            votesCount = 11,
            averageRating = 6.51
        )
        app.movieIsRated(
            eventId = eventId,
            id = "tt0463985",
            votesCount = 11,
            averageRating = 6.51
        )

        When("all details are fetched") {
            eventually {
                val response = app.handleRequest(Get, "/details").response

                Then("one movie details with rating updated") {
                    // language=JSON
                    response shouldHaveContent """[{
                        |"id":"tt0463985",
                        |"title":"The Fast and the Furious: Tokyo Drift",
                        |"description":"description",
                        |"releaseDate":"2006-06-16",
                        |"rating":"6.5",
                        |"votes":"11",
                        |"imdbRating":"6.1",
                        |"runtime":"104 min"
                        |}]""".trimMargin().toSingleLine()
                }
            }
        }
    }

    `Given movie details app`(" and movie is rated twice") { app ->
        app.movieIsAddedToCatalog(
            id = "tt0463985",
            title = "The Fast and the Furious: Tokyo Drift",
            description = "description",
            releaseDate = LocalDate.of(2006, JUNE, 16),
            runtime = Duration.ofMinutes(104),
            imdbRating = 6.1
        )
        app.movieIsRated(
            id = "tt0463985",
            votesCount = 11,
            averageRating = 6.51
        )
        delay(50)
        app.movieIsRated(
            id = "tt0463985",
            votesCount = 12,
            averageRating = 5.1
        )

        When("all details are fetched") {
            eventually {
                val response = app.handleRequest(Get, "/details").response

                Then("one movie details with rating updated") {
                    // language=JSON
                    response shouldHaveContent """[{
                        |"id":"tt0463985",
                        |"title":"The Fast and the Furious: Tokyo Drift",
                        |"description":"description",
                        |"releaseDate":"2006-06-16",
                        |"rating":"5.1",
                        |"votes":"12",
                        |"imdbRating":"6.1",
                        |"runtime":"104 min"
                        |}]""".trimMargin().toSingleLine()
                }
            }
        }

    }
    `Given movie details app`(" and movie is rated twice in wrong order") { app ->
        app.movieIsAddedToCatalog(
            id = "tt0463985",
            title = "The Fast and the Furious: Tokyo Drift",
            description = "description",
            releaseDate = LocalDate.of(2006, JUNE, 16),
            runtime = Duration.ofMinutes(104),
            imdbRating = 6.1
        )
        app.movieIsRated(
            id = "tt0463985",
            votesCount = 12,
            averageRating = 5.1
        )
        delay(50)
        app.movieIsRated(
            id = "tt0463985",
            votesCount = 11,
            averageRating = 6.51
        )
        When("all details are fetched") {
            eventually {
                val response = app.handleRequest(Get, "/details").response

                Then("one movie details with rating not updated") {
                    // language=JSON
                    response shouldHaveContent """[{
                        |"id":"tt0463985",
                        |"title":"The Fast and the Furious: Tokyo Drift",
                        |"description":"description",
                        |"releaseDate":"2006-06-16",
                        |"rating":"5.1",
                        |"votes":"12",
                        |"imdbRating":"6.1",
                        |"runtime":"104 min"
                        |}]""".trimMargin().toSingleLine()
                }
            }
        }

    }

}) {
    override fun listeners() = listOf(ktor)
}


private fun TestApplicationEngine.movieIsAddedToCatalog(
    id: String,
    title: String,
    description: String,
    releaseDate: LocalDate,
    runtime: Duration,
    imdbRating: Double
) {
    environment.monitor.raise(
        IntegrationEvent.MovieAddedToCatalogEvent,
        IntegrationEvent.MovieAddedToCatalogEvent(
            id = id,
            imdbId = id,
            title = title,
            description = description,
            releaseDate = releaseDate,
            imdbRating = imdbRating,
            imdbVotes = 259_218,
            runtime = runtime,
            timestamp = anyTime
        )
    )
}

private fun TestApplicationEngine.movieIsRated(
    id: String,
    votesCount: Int,
    averageRating: Double,
    eventId: String = UUID.randomUUID().toString()
) {
    environment.monitor.raise(
        IntegrationEvent.MovieRatedEvent,
        IntegrationEvent.MovieRatedEvent(
            id = eventId,
            movieId = id,
            newAverageRating = averageRating,
            newVotesCount = votesCount,
            timestamp = anyTime
        )
    )
}

private fun BehaviorSpec.`Given movie details app`(
    name: String,
    test: suspend BehaviorSpecGivenContainerScope.(TestApplicationEngine) -> Unit
) = GivenApp("ratings app $name", ktor.moduleTestApp, test)

private val ktor = KtorModuleTestSupport(Application::movieDetailsModule, Unit)

private val anyTime = Instant.parse("2007-12-03T10:15:30.00Z")

private fun String.toSingleLine() =
    replace("[\n\r]".toRegex(), "")