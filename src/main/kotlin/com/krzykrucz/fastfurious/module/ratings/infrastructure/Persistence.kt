package com.krzykrucz.fastfurious.module.ratings.infrastructure

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.krzykrucz.fastfurious.module.ratings.CinemaMovieRating
import com.krzykrucz.fastfurious.module.ratings.CurrentCinemaMovieRating
import com.krzykrucz.fastfurious.module.ratings.MovieId
import com.krzykrucz.fastfurious.module.ratings.Votes
import java.util.concurrent.ConcurrentHashMap

typealias FindCinemaMovieRating = suspend (MovieId) -> Either<PersistenceFailure, CurrentCinemaMovieRating>

val findCinemaMovieRating: FindCinemaMovieRating = { movieId ->
    try {
        cinemaMovieRatingsDb[movieId]
            ?.let(::CurrentCinemaMovieRating)
            ?.right()
            ?: PersistenceFailure.NoSuchMovie.left()
    } catch (error: Throwable) {
        PersistenceFailure.Unknown.left()
    }
}

typealias PersistCinemaMovieRating = suspend (CinemaMovieRating) -> Either<PersistenceFailure, Ok>

val persistCinemaMovieRating: PersistCinemaMovieRating = { movieRating ->
    try {
        val savedRating = cinemaMovieRatingsDb.compute(movieRating.movieId) { _, persistedRating ->
            if ((persistedRating?.votes ?: Votes.zero) == movieRating.votes - 1) movieRating
            else persistedRating
        }
        when (savedRating) {
            movieRating -> Unit.right()
            else -> PersistenceFailure.OptimisticLockViolated.left()
        }
    } catch (error: Throwable) {
        PersistenceFailure.Unknown.left()
    }
}

private val cinemaMovieRatingsDb: MutableMap<MovieId, CinemaMovieRating> = ConcurrentHashMap()


enum class PersistenceFailure {
    NoSuchMovie, Unknown, OptimisticLockViolated
}
typealias Ok = Unit