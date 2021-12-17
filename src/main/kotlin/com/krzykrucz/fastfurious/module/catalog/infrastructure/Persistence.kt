package com.krzykrucz.fastfurious.module.catalog.infrastructure

import arrow.core.Either
import arrow.core.right
import com.krzykrucz.fastfurious.module.catalog.IMDbId
import com.krzykrucz.fastfurious.module.catalog.NewMovie
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

typealias PersistMovieInCatalog = suspend (NewMovie) -> Either<Throwable, NewMovie>

val persistMovieInCatalog: PersistMovieInCatalog = { newMovie ->
    val catalogedMovie = with(newMovie) {
        CatalogedMovie(
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
    movieCatalogDb[newMovie.imdbId] = catalogedMovie
    newMovie.right()
}

typealias IsMoviePersistedInCatalog = suspend (IMDbId) -> Either<Throwable, Boolean>

val isMoviePersistedInCatalog: IsMoviePersistedInCatalog = { imdbId ->
    (imdbId in movieCatalogDb).right()
}

private data class CatalogedMovie(
    val imdbId: String,
    val title: String,
    val description: String,
    val releaseDate: LocalDate,
    val imdbRating: Double,
    val imdbVotes: Int,
    val runtime: Duration,
    val timestamp: Instant
)

private val movieCatalogDb: MutableMap<IMDbId, CatalogedMovie> = ConcurrentHashMap()

