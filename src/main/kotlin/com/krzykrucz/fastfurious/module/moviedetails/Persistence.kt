package com.krzykrucz.fastfurious.module.moviedetails

import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap


typealias GetAllMovieDetails = suspend () -> List<MovieDetails>

val getAllMovieDetails: GetAllMovieDetails = {
    movieDetailsView.values.toList()
}

typealias GetMovieDetails = suspend (MovieId) -> MovieDetails?

val getMovieDetails: GetMovieDetails = { movieId ->
    try {
        movieDetailsView[movieId]
    } catch (error: Throwable) {
        log.error("Error getting movie with id $movieId")
        null
    }
}

typealias PersistMovieDetails = suspend (MovieDetails) -> Unit?

val persistMovieDetails: PersistMovieDetails = { movieDetails ->
    try {
        movieDetailsView[movieDetails.id] = movieDetails
    } catch (error: Throwable) {
        log.error("Error persisting movie with id ${movieDetails.id}")
        null
    }
}


private val movieDetailsView: MutableMap<MovieId, MovieDetails> = ConcurrentHashMap()

private val log = LoggerFactory.getLogger("movieDetailsView")
