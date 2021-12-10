package com.krzykrucz.fastfurious.module.ratings

import com.krzykrucz.fastfurious.eventbus.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.eventbus.IntegrationEvent.MovieRatedEvent
import com.krzykrucz.fastfurious.eventbus.events
import com.krzykrucz.fastfurious.eventbus.publishAsync
import com.krzykrucz.fastfurious.eventbus.subscribeOn
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing

fun Application.ratingsModule() {

    subscribeOn(MovieAddedToCatalogEvent) {
    }

    routing {
        post("/rate") {
            events.publishAsync(MovieRatedEvent, MovieRatedEvent(""))

            call.respondText("Hello World!")
        }
    }
}