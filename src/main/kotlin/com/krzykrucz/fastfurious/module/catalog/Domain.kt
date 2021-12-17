package com.krzykrucz.fastfurious.module.catalog

import java.time.Duration
import java.time.Instant
import java.time.LocalDate


enum class IMDbId {
    tt0232500,
    tt0322259,
    tt0463985,
    tt1013752,
    tt1596343,
    tt1905041,
    tt2820852,
    tt4630562,
    tt5433138,
    ;

    override fun toString() = name
}

data class NewMovie(
    val imdbId: IMDbId,
    val title: Title,
    val description: Description,
    val releaseDate: LocalDate,
    val imdbRating: IMDbRating,
    val runtime: Runtime,
    val catalogedAt: Instant
)


data class IMDbRating(
    val rating: Rating,
    val votes: Votes
) {
    companion object {
        operator fun invoke(rating: Double, votes: Int): IMDbRating? =
            Rating(rating)
                ?.let { r -> Votes(votes)?.let { v -> r to v } }
                ?.let { IMDbRating(it.first, it.second) }
    }
}

@JvmInline
value class Runtime(val value: Duration)

@JvmInline
value class Rating private constructor(val value: Double) {
    companion object {
        operator fun invoke(rating: Double): Rating? =
            rating.takeIf { it in 1.0..10.0 }
                ?.let(::Rating)
    }
}

@JvmInline
value class Votes private constructor(val value: Int) {
    companion object {
        operator fun invoke(votes: Int): Votes? =
            votes.takeIf { it > 0 }
                ?.let(::Votes)
    }
}

@JvmInline
value class Title private constructor(val value: String) {
    companion object {
        operator fun invoke(title: String): Title? =
            title.takeIf(String::isNotBlank)
                ?.let(::Title)
    }
}

@JvmInline
value class Description private constructor(val value: String) {
    companion object {
        operator fun invoke(description: String): Description? =
            description.takeIf(String::isNotBlank)
                ?.let(::Description)
    }
}
