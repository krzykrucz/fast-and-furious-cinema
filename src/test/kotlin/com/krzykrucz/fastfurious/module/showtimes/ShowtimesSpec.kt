package com.krzykrucz.fastfurious.module.showtimes

import com.krzykrucz.fastfurious.monolith.IntegrationEvent
import com.krzykrucz.fastfurious.support.GivenApp
import com.krzykrucz.fastfurious.support.KtorModuleTestSupport
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.kotest.matchers.string.shouldMatch
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId

class ShowtimesSpec : BehaviorSpec({

    `Given showtimes app`(" and movie $someMovieTitle is added to catalog") { app ->
        app.movieIsAddedToCatalog(someMovieTitle)

        var showId: String? = null
        When("show is created") {
            val response = app.handleRequest(HttpMethod.Post, "/showtimes/schedule") {
                addHeader("Content-Type", "application/json")
                setBody(
                    """
                  {
                    "title": "$someMovieTitle",
                    "showTime": "$showTime",
                    "runtime": "$anyRuntime",
                    "price": "$price"
                  }
                  """.trimIndent()
                )
            }.response

            Then("response status is ok") {
                response shouldHaveStatus OK
                response.content shouldMatch "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})".toRegex()
                showId = response.content
            }
            Then("showtimes view contains this show") {
                val view = app.handleRequest(HttpMethod.Get, "/showtimes").response
                view shouldHaveStatus OK
                view.content shouldMatchJson """
                   [
                      {
                        "day": "2007-12-04",
                        "showtimes": {
                          "The Fast and the Furious: Tokyo Drift": [
                            "2007-12-04T11:15:30"
                          ]
                        }
                      }
                    ]
                """.trimIndent()
            }
        }

        When("show is updated") {
            val response = app.handleRequest(HttpMethod.Put, "/showtimes/schedule/$showId") {
                addHeader("Content-Type", "application/json")
                setBody(
                    """
                  {
                    "startTime": "$showTimeHourLater",
                    "price": "$priceDollarBigger"
                  }""".trimIndent()
                )
            }.response

            Then("response status is ok") {
                response shouldHaveStatus OK
            }

            Then("showtimes view contains updated show") {
                val view = app.handleRequest(HttpMethod.Get, "/showtimes").response
                view shouldHaveStatus OK
                view.content shouldMatchJson """
                   [
                      {
                        "day": "2007-12-04",
                        "showtimes": {
                          "The Fast and the Furious: Tokyo Drift": [
                            "2007-12-04T12:15:30"
                          ]
                        }
                      }
                    ]
                """.trimIndent()

            }
        }
    }
}) {
    override fun listeners() = listOf(ktor)
    override fun isolationMode() = IsolationMode.SingleInstance
}

private fun TestApplicationEngine.movieIsAddedToCatalog(title: String) {
    environment.monitor.raise(
        IntegrationEvent.MovieAddedToCatalogEvent,
        IntegrationEvent.MovieAddedToCatalogEvent(
            id = "tt0463985",
            imdbId = "tt0463985",
            title = title,
            description = "A teenager becomes a major competitor in the world of drift racing after moving in with his father in Tokyo to avoid a jail sentence in America.",
            releaseDate = LocalDate.of(2006, Month.JUNE, 16),
            imdbRating = 6.0,
            imdbVotes = 259_218,
            runtime = Duration.ofMinutes(104),
            timestamp = currentTime - Duration.ofSeconds(1)
        )
    )
}

private val currentTime = Instant.parse("2007-12-03T10:15:30.00Z")

private const val someMovieTitle = "The Fast and the Furious: Tokyo Drift"

private const val anyRuntime = "PT1h"

private const val price = "11.54"
private const val priceDollarBigger = "12.54"
private const val showTime = "2007-12-04T11:15:30.00"
private const val showTimeHourLater = "2007-12-04T12:15:30.00"

private val testClock = Clock.fixed(currentTime, ZoneId.systemDefault())

private val ktor = KtorModuleTestSupport(Application::showtimesModule, testClock)

private fun BehaviorSpec.`Given showtimes app`(
    name: String,
    test: suspend BehaviorSpecGivenContainerScope.(TestApplicationEngine) -> Unit
) = GivenApp("showtimes app $name", ktor.moduleTestApp, test)
