package com.krzykrucz.fastfurious.module.moviedetails

import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieRatedEvent
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP


@JvmInline
value class MovieId(val id: String)

data class MovieDetails(
    val id: MovieId,
    val title: String,
    val description: String,
    val releaseDate: String,
    val rating: String,
    val votes: String,
    val imdbRating: String,
    val runtime: String
)

fun apply(movieAddedToCatalogEvent: MovieAddedToCatalogEvent): MovieDetails =
    with(movieAddedToCatalogEvent) {
        MovieDetails(
            id = MovieId(imdbId),
            title = title,
            description = description,
            releaseDate = releaseDate.toString(),
            imdbRating = imdbRating.toString(),
            rating = "0.0",
            votes = "0",
            runtime = "${runtime.toMinutes()} min"
        )
    }

fun MovieDetails.apply(movieRatedEvent: MovieRatedEvent): MovieDetails =
    if (movieRatedEvent.newVotesCount > votes.toInt())
        copy(
            rating = BigDecimal(movieRatedEvent.newAverageRating)
                .setScale(1, HALF_UP)
                .toPlainString(),
            votes = movieRatedEvent.newVotesCount.toString()
        )
    else this
