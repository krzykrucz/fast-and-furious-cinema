package com.krzykrucz.fastfurious.module.ratings

import com.krzykrucz.fastfurious.module.ratings.infrastructure.UUIDProvider
import com.krzykrucz.fastfurious.monolith.IntegrationEvent
import com.krzykrucz.fastfurious.support.GivenApp
import com.krzykrucz.fastfurious.support.KtorModuleTestSupport
import io.kotest.assertions.ktor.shouldHaveContent
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.mockk.every
import io.mockk.mockk
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.util.UUID

class RatingsSpec : BehaviorSpec({

    `Given ratings app`(" and movies $someMovieId and $anotherMovieId are added to catalog") { app ->
        app.movieIsAddedToCatalog(someMovieId)
        app.movieIsAddedToCatalog(anotherMovieId)

        When("movie $someMovieId is rated") {
            val response = app.handleRequest(HttpMethod.Post, "/rate/$someMovieId/10").response

            Then("response status is ok") {
                response shouldHaveStatus HttpStatusCode.OK
            }
            Then("movie rated event is published") {
                ktor.eventShouldBePublished(
                    IntegrationEvent.MovieRatedEvent(
                        id = firstUUID.toString(),
                        movieId = someMovieId,
                        newAverageRating = 10.0,
                        newVotesCount = 1,
                        timestamp = currentTime
                    )
                )
            }
        }
        When("movie $anotherMovieId is rated") {
            val response = app.handleRequest(HttpMethod.Post, "/rate/$someMovieId/5").response

            Then("response status is ok") {
                response shouldHaveStatus HttpStatusCode.OK
            }
            Then("movie rated event is published") {
                ktor.eventShouldBePublished(
                    IntegrationEvent.MovieRatedEvent(
                        id = secondUUID.toString(),
                        movieId = someMovieId,
                        newAverageRating = 7.5,
                        newVotesCount = 2,
                        timestamp = currentTime
                    )
                )
            }
        }

        When("there is a call with invalid movie id") {
            val response = app.handleRequest(HttpMethod.Post, "/rate/f$someMovieId/5").response

            Then("response status is bad request") {
                response shouldHaveStatus HttpStatusCode.BadRequest
                response shouldHaveContent "movie id param is invalid"
            }
        }
    }
    `Given ratings app`(" and empty catalog") { app ->

        When("there is a call with invalid rating") {
            val response = app.handleRequest(HttpMethod.Post, "/rate/$unknownMovieId/10").response

            Then("response status is 5xx") {
                response shouldHaveStatus HttpStatusCode.InternalServerError
                response shouldHaveContent "NoSuchMovie"
            }
            Then("no events are published") {
                ktor.noEventsShouldBePublished()
            }
        }
    }
}) {
    override fun listeners() = listOf(ktor)

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        every { uuidProvider() } returnsMany listOf(firstUUID, secondUUID)
    }
}

private fun TestApplicationEngine.movieIsAddedToCatalog(id: String) {
    environment.monitor.raise(
        IntegrationEvent.MovieAddedToCatalogEvent,
        IntegrationEvent.MovieAddedToCatalogEvent(
            id = id,
            imdbId = id,
            title = "The Fast and the Furious: Tokyo Drift",
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

private const val someMovieId = "tt0463985"
private const val anotherMovieId = "tt1013752"
private const val unknownMovieId = "tt1905041"

private val firstUUID = UUID.fromString("b236a1b3-923d-42b1-a872-2eb1a2f8ef5d")
private val secondUUID = UUID.fromString("c0a0eff8-0b19-40ec-8286-a0ec712cdd39")

private val uuidProvider = mockk<UUIDProvider>()

private val testConfig = RatingsConfig(
    clock = Clock.fixed(currentTime, ZoneId.systemDefault()),
    uuidProvider = uuidProvider
)


private val ktor = KtorModuleTestSupport(Application::ratingsModule, testConfig)

private fun BehaviorSpec.`Given ratings app`(
    name: String,
    test: suspend BehaviorSpecGivenContainerScope.(TestApplicationEngine) -> Unit
) = GivenApp("ratings app $name", ktor.moduleTestApp, test)
