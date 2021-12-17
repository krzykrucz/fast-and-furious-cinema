package com.krzykrucz.fastfurious.module.catalog

import arrow.core.identity
import arrow.core.zip
import com.krzykrucz.fastfurious.module.catalog.infrastructure.fetchMovieInfo
import com.krzykrucz.fastfurious.module.catalog.infrastructure.isMoviePersistedInCatalog
import com.krzykrucz.fastfurious.module.catalog.infrastructure.persistMovieInCatalog
import com.krzykrucz.fastfurious.module.catalog.infrastructure.publishMovieAddedToCatalogEvent
import com.krzykrucz.fastfurious.monolith.EnvironmentName
import com.krzykrucz.fastfurious.monolith.events
import io.github.resilience4j.kotlin.retry.RetryConfig
import io.github.resilience4j.retry.Retry
import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.Logging
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking

fun Application.catalogModule(env: EnvironmentName, configs: Map<EnvironmentName, CatalogConfig> = catalogConfig) {
    val config = configs[env]!!
    val httpClient = HttpClient {
        install(Logging)
        install(JsonFeature)
    }
    val retry = Retry.of("omdb", RetryConfig<HttpResponse> {
        maxAttempts(1 + config.omdbRetries)
    })

    val fetchMovieInfo = fetchMovieInfo(config, httpClient, retry)
    val publishMovieAddedToCatalogEvent = publishMovieAddedToCatalogEvent(events)
    val addMovieToCatalog = addMovieToCatalog(
        fetchMovieInfo, isMoviePersistedInCatalog, persistMovieInCatalog, publishMovieAddedToCatalogEvent
    )

    runBlocking {
        addAllFastAndFuriousMoviesToCatalog(addMovieToCatalog)
    }
}

private suspend fun addAllFastAndFuriousMoviesToCatalog(addMovieToCatalog: AddMovieToCatalog): Unit =
    IMDbId.values()
        .map { imdbId -> addMovieToCatalog(imdbId) }
        .reduceRight { either1, accEither ->
            accEither.zip(either1) { _, _ -> }
        }.fold({ throw it }, ::identity)

