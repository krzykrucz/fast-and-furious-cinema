package com.krzykrucz.fastfurious.module.ratings

import arrow.core.flatMap
import arrow.core.partially1
import com.krzykrucz.fastfurious.module.ratings.infrastructure.PersistenceFailure.OptimisticLockViolated
import com.krzykrucz.fastfurious.module.ratings.infrastructure.findCinemaMovieRating
import com.krzykrucz.fastfurious.module.ratings.infrastructure.persistCinemaMovieRating
import com.krzykrucz.fastfurious.module.ratings.infrastructure.publishEvent
import com.krzykrucz.fastfurious.monolith.EnvironmentName
import com.krzykrucz.fastfurious.monolith.EnvironmentName.LOCAL
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.events
import com.krzykrucz.fastfurious.monolith.publishAsync
import com.krzykrucz.fastfurious.monolith.subscribeOn
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing

fun Application.ratingsModule(
    environmentName: EnvironmentName,
    configs: Map<EnvironmentName, RatingsConfig> = mapOf(LOCAL to RatingsConfig())
) {
    val (clock, uuidProvider) = configs[environmentName]!!
    val publishEvent = publishEvent(events, uuidProvider)
    val rateMovie = rateMovie.partially1(clock)

    subscribeOn(MovieAddedToCatalogEvent) { event ->
        val newMovie = NewMovie(MovieId(event.id)!!)
        val movieRating = createCinemaMovieRating(newMovie)
        persistCinemaMovieRating(movieRating)
            .tapLeft { if (it == OptimisticLockViolated) events.publishAsync(MovieAddedToCatalogEvent, event) }
    }

    routing {
        post("/rate/{movieId}/{rating}") {
            val moviegoerRating = context.parameters["rating"]
                ?.let(MoviegoerRating::invoke)
                ?: return@post call.respond(HttpStatusCode.BadRequest, "rating param is invalid")
            val movieId =
                context.parameters["movieId"]
                    ?.let(MovieId::invoke)
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "movie id param is invalid")

            findCinemaMovieRating(movieId)
                .map { cinemaRating -> rateMovie(cinemaRating, moviegoerRating) }
                .flatMap { event -> persistCinemaMovieRating(event.newCinemaMovieRating.rating).map { it to event } }
                .map { (_, event) -> publishEvent(event) }
                .fold(
                    { call.respond(HttpStatusCode.InternalServerError, it.name) },
                    { call.respond(HttpStatusCode.OK, "Your rating is counted!") })
        }
    }
}