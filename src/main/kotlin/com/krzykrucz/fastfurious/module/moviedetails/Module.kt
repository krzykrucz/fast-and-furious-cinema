package com.krzykrucz.fastfurious.module.moviedetails

import com.krzykrucz.fastfurious.monolith.EnvironmentName
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.IntegrationEvent.MovieRatedEvent
import com.krzykrucz.fastfurious.monolith.subscribeOn
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.movieDetailsModule(environmentName: EnvironmentName, configs: Map<EnvironmentName, Unit> = emptyMap()) {

    install(ContentNegotiation, ContentNegotiation.Configuration::gson)

    subscribeOn(MovieAddedToCatalogEvent) { event ->
        val movieDetails = apply(event)
        persistMovieDetails(movieDetails)
    }

    subscribeOn(MovieRatedEvent) { event ->
        MovieId(event.movieId)
            .let { movieId -> getMovieDetails(movieId) }
            ?.apply(event)
            ?.let { updatedMovieDetails -> persistMovieDetails(updatedMovieDetails) }
    }

    routing {
        get("/details") {
            call.respond(HttpStatusCode.OK, getAllMovieDetails())
        }
    }
}