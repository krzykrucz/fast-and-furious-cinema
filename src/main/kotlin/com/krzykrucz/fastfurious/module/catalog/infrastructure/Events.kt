package com.krzykrucz.fastfurious.module.catalog.infrastructure

import arrow.core.Either
import com.krzykrucz.fastfurious.module.catalog.NewMovie
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.publishAsync
import io.ktor.application.ApplicationEvents

typealias PublishMovieAddedToCatalogEvent = suspend (NewMovie) -> Either<Throwable, Unit>

fun publishMovieAddedToCatalogEvent(applicationEvents: ApplicationEvents): PublishMovieAddedToCatalogEvent =
    { newMovie ->
        val event = with(newMovie) {
            MovieAddedToCatalogEvent(
                id = imdbId.name,
                imdbId = imdbId.name,
                title = title.value,
                description = description.value,
                releaseDate = releaseDate,
                imdbRating = imdbRating.rating.value,
                imdbVotes = imdbRating.votes.value,
                runtime = runtime.value,
                timestamp = catalogedAt
            )
        }
        applicationEvents.publishAsync(MovieAddedToCatalogEvent, event)
    }
