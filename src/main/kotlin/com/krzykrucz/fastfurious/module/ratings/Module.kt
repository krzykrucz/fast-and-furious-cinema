package com.krzykrucz.fastfurious.module.ratings

import com.krzykrucz.fastfurious.monolith.EnvironmentName
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieRatedEvent
import com.krzykrucz.fastfurious.monolith.events
import com.krzykrucz.fastfurious.monolith.publishAsync
import com.krzykrucz.fastfurious.monolith.subscribeOn
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing

fun Application.ratingsModule(environmentName: EnvironmentName, config: Map<EnvironmentName, String> = emptyMap()) {

    subscribeOn(MovieAddedToCatalogEvent) {
        println(it)
    }

    routing {
        post("/rate") {
            events.publishAsync(MovieRatedEvent, MovieRatedEvent(""))

            call.respondText("Hello World!")
        }
    }
}