package com.krzykrucz.fastfurious.module.moviedetails

import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieRatedEvent
import com.krzykrucz.fastfurious.monolith.subscribeOn
import io.ktor.application.Application
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.movieDetailsModule() {

    subscribeOn(MovieAddedToCatalogEvent) {
    }

    subscribeOn(MovieRatedEvent) {
    }


    routing {
        get("/details") {

        }
    }
}