package com.krzykrucz.fastfurious.module.ratings

import arrow.core.partially1
import java.time.Clock
import java.time.Instant


typealias CreateMovieRating = (NewMovie) -> CinemaMovieRating

val createCinemaMovieRating: CreateMovieRating = { newMovie ->
    CinemaMovieRating(newMovie.movieId, Votes.zero, AverageRating.zero)
}


typealias RateMovie = (Clock, CurrentCinemaMovieRating, MoviegoerRating) -> MovieRatedEvent

val rateMovie: RateMovie = { clock, currentMovieRating, moviegoerRating ->
    val createEvent = ::createEvent.partially1(clock)

    applyMoviegoerRating(currentMovieRating, moviegoerRating)
        .let { createEvent(it) }
}

private fun applyMoviegoerRating(
    currentCinemaMovieRating: CurrentCinemaMovieRating,
    moviegoerRating: MoviegoerRating
): NewCinemaMovieRating =
    with(currentCinemaMovieRating.rating) {
        copy(
            votes = votes + 1,
            average = AverageRating((votes.value * average.value + moviegoerRating.value) / (votes.value + 1))!!
        )
    }
        .let(::NewCinemaMovieRating)


private fun createEvent(
    clock: Clock,
    newCinemaMovieRating: NewCinemaMovieRating
): MovieRatedEvent =
    MovieRatedEvent(
        movieId = newCinemaMovieRating.rating.movieId,
        timestamp = clock.instant(),
        newCinemaMovieRating = newCinemaMovieRating
    )

data class CinemaMovieRating(
    val movieId: MovieId,
    val votes: Votes,
    val average: AverageRating
)

@JvmInline
value class CurrentCinemaMovieRating(val rating: CinemaMovieRating)

@JvmInline
value class NewCinemaMovieRating(val rating: CinemaMovieRating)

data class MovieRatedEvent(
    val movieId: MovieId,
    val timestamp: Instant,
    val newCinemaMovieRating: NewCinemaMovieRating
)

data class NewMovie(
    val movieId: MovieId
)

@JvmInline
value class MovieId private constructor(val value: String) {
    companion object {
        operator fun invoke(id: String): MovieId? =
            id.takeIf { it matches "tt\\d{7}".toRegex() }
                ?.let(::MovieId)
    }
}

@JvmInline
value class AverageRating private constructor(val value: Double) {

    companion object {
        operator fun invoke(rating: Double): AverageRating? =
            rating.takeIf { it in 0.0..10.0 }
                ?.let(::AverageRating)

        val zero = AverageRating(0.0)
    }
}

enum class MoviegoerRating {
    `1`, `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, `10`;

    val value = name.toInt()

    companion object {
        operator fun invoke(string: String): MoviegoerRating? =
            values().find { it.name == string }
    }
}


@JvmInline
value class Votes private constructor(val value: Int) {

    operator fun plus(votes: Int) = Votes(value + votes)

    companion object {
        operator fun invoke(votes: Int): Votes? =
            votes.takeIf { it > 0 }
                ?.let(::Votes)

        val zero = Votes(0)
    }
}