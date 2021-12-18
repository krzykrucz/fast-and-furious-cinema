package com.krzykrucz.fastfurious.module.ratings.infrastructure

import com.krzykrucz.fastfurious.module.ratings.CinemaMovieRating
import com.krzykrucz.fastfurious.module.ratings.CurrentCinemaMovieRating
import com.krzykrucz.fastfurious.module.ratings.MovieId
import java.util.concurrent.ConcurrentHashMap

typealias FindMovieRating = suspend (MovieId) -> CurrentCinemaMovieRating?

val findMovieRating: FindMovieRating = { movieId ->
    cinemaMovieRatingsDb[movieId]
        ?.let(::CurrentCinemaMovieRating)
}

typealias PersistMovieRating = suspend (CinemaMovieRating) -> Unit

val persistMovieRating: PersistMovieRating = { movieRating ->
    cinemaMovieRatingsDb[movieRating.movieId] = movieRating
}

private val cinemaMovieRatingsDb: MutableMap<MovieId, CinemaMovieRating> = ConcurrentHashMap()