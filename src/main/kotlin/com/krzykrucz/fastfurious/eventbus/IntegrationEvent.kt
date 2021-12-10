package com.krzykrucz.fastfurious.eventbus

import io.ktor.application.EventDefinition

sealed class IntegrationEvent {

    abstract val id: String

    data class MovieAddedToCatalogEvent(override val id: String) : IntegrationEvent() {
        companion object : EventDefinition<MovieAddedToCatalogEvent>()
    }

    data class MovieRatedEvent(override val id: String) : IntegrationEvent() {
        companion object : EventDefinition<MovieRatedEvent>()

    }
}
