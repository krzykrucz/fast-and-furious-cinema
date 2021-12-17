package com.krzykrucz.fastfurious.module.catalog

import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.support.GivenApp
import com.krzykrucz.fastfurious.support.KtorModuleTestSupport
import com.krzykrucz.fastfurious.support.OMDbApiMockTestSupport
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationEngine
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Month.APRIL
import java.time.Month.JUNE
import java.time.Month.MAY
import java.time.ZoneId

class CatalogSpec : BehaviorSpec({

    `Given catalog app`("") { app ->

        When("app is running") {

            Then("all movies are downloaded once") {
                omdbApiMock.verifyAllMoviesDownloadedOnce()
            }

            Then("all Fast&Furious movies are added to the catalog") {
                ktor.eventsShouldBePublished(
                    MovieAddedToCatalogEvent(
                        id = "tt0232500",
                        imdbId = "tt0232500",
                        title = "The Fast and the Furious",
                        description = "Los Angeles police officer Brian O'Conner must decide where his loyalty really lies when he becomes enamored with the street racing world he has been sent undercover to destroy.",
                        releaseDate = LocalDate.of(2001, JUNE, 22),
                        imdbRating = 6.8,
                        imdbVotes = 370_116,
                        runtime = Duration.ofMinutes(106),
                        timestamp = currentTime
                    ),
                    MovieAddedToCatalogEvent(
                        id = "tt0322259",
                        imdbId = "tt0322259",
                        title = "2 Fast 2 Furious",
                        description = "Former cop Brian O'Conner is called upon to bust a dangerous criminal and he recruits the help of a former childhood friend and street racer who has a chance to redeem himself.",
                        releaseDate = LocalDate.of(2003, JUNE, 6),
                        imdbRating = 5.9,
                        imdbVotes = 264_489,
                        runtime = Duration.ofMinutes(107),
                        timestamp = currentTime
                    ),
                    MovieAddedToCatalogEvent(
                        id = "tt0463985",
                        imdbId = "tt0463985",
                        title = "The Fast and the Furious: Tokyo Drift",
                        description = "A teenager becomes a major competitor in the world of drift racing after moving in with his father in Tokyo to avoid a jail sentence in America.",
                        releaseDate = LocalDate.of(2006, JUNE, 16),
                        imdbRating = 6.0,
                        imdbVotes = 259_218,
                        runtime = Duration.ofMinutes(104),
                        timestamp = currentTime
                    ),
                    MovieAddedToCatalogEvent(
                        id = "tt1013752",
                        imdbId = "tt1013752",
                        title = "Fast & Furious",
                        description = "Brian O'Conner, back working for the FBI in Los Angeles, teams up with Dominic Toretto to bring down a heroin importer by infiltrating his operation.",
                        releaseDate = LocalDate.of(2009, APRIL, 3),
                        imdbRating = 6.6,
                        imdbVotes = 276_684,
                        runtime = Duration.ofMinutes(107),
                        timestamp = currentTime
                    ),
                    MovieAddedToCatalogEvent(
                        id = "tt1596343",
                        imdbId = "tt1596343",
                        title = "Fast Five",
                        description = "Dominic Toretto and his crew of street racers plan a massive heist to buy their freedom while in the sights of a powerful Brazilian drug lord and a dangerous federal agent.",
                        releaseDate = LocalDate.of(2011, APRIL, 29),
                        imdbRating = 7.3,
                        imdbVotes = 368_801,
                        runtime = Duration.ofMinutes(130),
                        timestamp = currentTime
                    ),
                    MovieAddedToCatalogEvent(
                        id = "tt1905041",
                        imdbId = "tt1905041",
                        title = "Fast & Furious 6",
                        description = "Hobbs has Dominic and Brian reassemble their crew to take down a team of mercenaries: Dominic unexpectedly gets sidetracked with facing his presumed deceased girlfriend, Letty.",
                        releaseDate = LocalDate.of(2013, MAY, 24),
                        imdbRating = 7.0,
                        imdbVotes = 383_258,
                        runtime = Duration.ofMinutes(130),
                        timestamp = currentTime
                    ),
                    MovieAddedToCatalogEvent(
                        id = "tt2820852",
                        imdbId = "tt2820852",
                        title = "Furious 7",
                        description = "Deckard Shaw seeks revenge against Dominic Toretto and his family for his comatose brother.",
                        releaseDate = LocalDate.of(2015, APRIL, 3),
                        imdbRating = 7.1,
                        imdbVotes = 376_958,
                        runtime = Duration.ofMinutes(137),
                        timestamp = currentTime
                    ),
                    MovieAddedToCatalogEvent(
                        id = "tt4630562",
                        imdbId = "tt4630562",
                        title = "The Fate of the Furious",
                        description = "When a mysterious woman seduces Dominic Toretto into the world of terrorism and a betrayal of those closest to him, the crew face trials that will test them as never before.",
                        releaseDate = LocalDate.of(2017, APRIL, 14),
                        imdbRating = 6.6,
                        imdbVotes = 221_120,
                        runtime = Duration.ofMinutes(136),
                        timestamp = currentTime
                    ),
                    MovieAddedToCatalogEvent(
                        id = "tt5433138",
                        imdbId = "tt5433138",
                        title = "F9: The Fast Saga",
                        description = "Dom and the crew must take on an international terrorist who turns out to be Dom and Mia's estranged brother.",
                        releaseDate = LocalDate.of(2021, JUNE, 25),
                        imdbRating = 5.2,
                        imdbVotes = 106_141,
                        runtime = Duration.ofMinutes(143),
                        timestamp = currentTime
                    ),
                )
            }
        }
    }
}) {
    override fun listeners() = listOf(omdbApiMock, ktor)
}


private fun BehaviorSpec.`Given catalog app`(
    name: String,
    test: suspend BehaviorSpecGivenContainerScope.(TestApplicationEngine) -> Unit
) = GivenApp("ratings app $name", ktor.moduleTestApp, test)

private val currentTime = Instant.parse("2007-12-03T10:15:30.00Z")

private val omdbApiMock = OMDbApiMockTestSupport()

private val testConfig = catalogConfig {
    clock = Clock.fixed(currentTime, ZoneId.systemDefault())
    omdbApiKey = omdbApiMock.apiKey
    omdbBaseUrl = omdbApiMock.baseUrl
}

private val ktor = KtorModuleTestSupport(Application::catalogModule, testConfig)
