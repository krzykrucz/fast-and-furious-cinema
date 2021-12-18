package com.krzykrucz.fastfurious.module.ratings

import arrow.core.partially1
import com.krzykrucz.fastfurious.module.ratings.infrastructure.findMovieRating
import com.krzykrucz.fastfurious.module.ratings.infrastructure.persistMovieRating
import com.krzykrucz.fastfurious.module.ratings.infrastructure.publishEvent
import com.krzykrucz.fastfurious.monolith.EnvironmentName
import com.krzykrucz.fastfurious.monolith.EnvironmentName.LOCAL
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.events
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

    subscribeOn(MovieAddedToCatalogEvent) {
        val newMovie = NewMovie(MovieId(it.id)!!)
        val movieRating = createCinemaMovieRating(newMovie)
        persistMovieRating(movieRating)
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

            findMovieRating(movieId)
                ?.let { rateMovie(it, moviegoerRating) }
                ?.also { persistMovieRating(it.newCinemaMovieRating.rating) }
                ?.let { publishEvent(it) }
                ?.let { call.respond(HttpStatusCode.OK, "Your rating is counted!") }
                ?: let { call.respond(HttpStatusCode.InternalServerError, "Error") }
        }
    }
}