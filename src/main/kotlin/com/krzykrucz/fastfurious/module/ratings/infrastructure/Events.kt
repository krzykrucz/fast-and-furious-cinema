package com.krzykrucz.fastfurious.module.ratings.infrastructure

import com.krzykrucz.fastfurious.module.ratings.MovieRatedEvent
import com.krzykrucz.fastfurious.monolith.IntegrationEvent
import com.krzykrucz.fastfurious.monolith.publishAsync
import io.ktor.application.ApplicationEvents
import java.util.UUID


typealias PublishEvent = suspend (MovieRatedEvent) -> Unit

typealias UUIDProvider = () -> UUID

fun publishEvent(events: ApplicationEvents, uuidProvider: UUIDProvider): PublishEvent = { event ->
    val integrationEvent = IntegrationEvent.MovieRatedEvent(
        id = uuidProvider().toString(),
        movieId = event.movieId.value,
        averageRatingChange = event.ratingChange,
        timestamp = event.timestamp
    )
    events.publishAsync(IntegrationEvent.MovieRatedEvent, integrationEvent)
}