package com.krzykrucz.fastfurious.module.showtimes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.showtimesModule() {


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}