package com.krzykrucz.fastfurious

import com.krzykrucz.fastfurious.module.catalog.catalogModule
import com.krzykrucz.fastfurious.module.moviedetails.movieDetailsModule
import com.krzykrucz.fastfurious.module.ratings.ratingsModule
import com.krzykrucz.fastfurious.module.showtimes.showtimesModule
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.request.path
import io.ktor.serialization.json
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(CallLogging) {
            level = Level.INFO
            filter { call -> call.request.path().startsWith("/") }
        }
        install(ContentNegotiation) {
            json()
        }

        catalogModule()
        showtimesModule()
        ratingsModule()
        movieDetailsModule()

    }.start(wait = true)
}
