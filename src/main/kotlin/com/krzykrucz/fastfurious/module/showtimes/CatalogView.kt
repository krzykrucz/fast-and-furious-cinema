package com.krzykrucz.fastfurious.module.showtimes

import org.slf4j.LoggerFactory

data class MovieCatalog(val movies: Set<Movie>)

data class Movie(
    val title: Title,
    val runtime: Runtime
)

typealias GetMovieCatalog = suspend () -> MovieCatalog

val getMovieCatalog: GetMovieCatalog = {
    try {
        MovieCatalog(catalog)
    } catch (error: Throwable) {
        log.error("Error getting movie catalog view", error)
        MovieCatalog(emptySet())
    }
}
typealias AddMovieToCatalog = suspend (Movie) -> Unit?

val addMovieToCatalog: AddMovieToCatalog = { movie ->
    try {
        synchronized(CatalogLock) {
            catalog.add(movie)
        }
            .takeIf { it }
            ?.let { }
    } catch (error: Throwable) {
        log.error("Error adding movie to catalog view", error)
        null
    }
}

private val log = LoggerFactory.getLogger(MovieCatalog::class.java)

private object CatalogLock

private val catalog: MutableSet<Movie> = mutableSetOf()