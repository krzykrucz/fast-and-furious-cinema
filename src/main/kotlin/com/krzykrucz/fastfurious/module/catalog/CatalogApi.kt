package com.krzykrucz.fastfurious.module.catalog

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.krzykrucz.fastfurious.module.catalog.infrastructure.FetchMovieInfo
import com.krzykrucz.fastfurious.module.catalog.infrastructure.IsMoviePersistedInCatalog
import com.krzykrucz.fastfurious.module.catalog.infrastructure.PersistMovieInCatalog
import com.krzykrucz.fastfurious.module.catalog.infrastructure.PublishMovieAddedToCatalogEvent
import org.slf4j.LoggerFactory


typealias AddMovieToCatalog = suspend (IMDbId) -> Either<Throwable, Unit>

fun addMovieToCatalog(
    fetchMovieInfo: FetchMovieInfo,
    isMoviePersistedInCatalog: IsMoviePersistedInCatalog,
    persistMovieInCatalog: PersistMovieInCatalog,
    publishMovieAddedToCatalogEvent: PublishMovieAddedToCatalogEvent
): AddMovieToCatalog = { imdbId ->
    isMoviePersistedInCatalog(imdbId)
        .flatMap { exists ->
            if (!exists) {
                fetchMovieInfo(imdbId)
                    .flatMap { persistMovieInCatalog(it) }
                    .flatMap { publishMovieAddedToCatalogEvent(it) }
            } else Unit.right()
        }
        .logError(imdbId)
}

private fun Either<Throwable, Unit>.logError(imDbId: IMDbId): Either<Throwable, Unit> =
    mapLeft { error ->
        log.error("Error adding movie to catalog with imdb id: $imDbId", error)
        error
    }

private val log = LoggerFactory.getLogger("AddMovieToCatalog")