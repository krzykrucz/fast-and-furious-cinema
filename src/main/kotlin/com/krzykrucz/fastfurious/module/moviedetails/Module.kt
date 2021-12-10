package com.krzykrucz.fastfurious.module.moviedetails

import com.krzykrucz.fastfurious.eventbus.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.eventbus.IntegrationEvent.MovieRatedEvent
import com.krzykrucz.fastfurious.eventbus.subscribeOn
import io.ktor.application.Application
import io.ktor.routing.routing

fun Application.movieDetailsModule() {

    subscribeOn(MovieAddedToCatalogEvent) {
    }

    subscribeOn(MovieRatedEvent) {
    }


    routing {

    }
}