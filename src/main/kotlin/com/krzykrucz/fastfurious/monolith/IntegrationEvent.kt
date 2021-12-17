package com.krzykrucz.fastfurious.monolith

import io.ktor.application.EventDefinition
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

sealed class IntegrationEvent {

    abstract val id: String

    data class MovieAddedToCatalogEvent(
        override val id: String,
        val imdbId: String,
        val title: String,
        val description: String,
        val releaseDate: LocalDate,
        val imdbRating: Double,
        val imdbVotes: Int,
        val runtime: Duration,
        val timestamp: Instant
    ) : IntegrationEvent() {
        companion object : EventDefinition<MovieAddedToCatalogEvent>()
    }

    data class MovieRatedEvent(override val id: String) : IntegrationEvent() {
        companion object : EventDefinition<MovieRatedEvent>()

    }
}
