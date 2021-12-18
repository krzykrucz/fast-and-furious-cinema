package com.krzykrucz.fastfurious.module.showtimes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.showtimesModule() {
    install(ContentNegotiation, ContentNegotiation.Configuration::gson)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}