package com.krzykrucz.fastfurious.module.catalog.infrastructure

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.krzykrucz.fastfurious.module.catalog.CatalogConfig
import com.krzykrucz.fastfurious.module.catalog.Description
import com.krzykrucz.fastfurious.module.catalog.IMDbId
import com.krzykrucz.fastfurious.module.catalog.IMDbRating
import com.krzykrucz.fastfurious.module.catalog.NewMovie
import com.krzykrucz.fastfurious.module.catalog.Runtime
import com.krzykrucz.fastfurious.module.catalog.Title
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias FetchMovieInfo = suspend (IMDbId) -> Either<Throwable, NewMovie>


fun fetchMovieInfo(config: CatalogConfig, httpClient: HttpClient, retry: Retry): FetchMovieInfo = { imdbId ->
    try {
        val response: MovieInfoDto = retry.executeSuspendFunction {
            httpClient.get("${config.omdbBaseUrl}/?apikey=${config.omdbApiKey}&i=$imdbId")
        }
        with(response) {
            NewMovie(
                imdbId = imdbId,
                title = Title(this.Title)
                    ?: return@with inconsistentData("title"),
                description = Description(Plot)
                    ?: return@with inconsistentData("plot"),
                releaseDate = parseDate(Released)
                    ?: return@with inconsistentData("release date"),
                imdbRating = parseBigInt(imdbVotes)?.let { IMDbRating(imdbRating, it) }
                    ?: return@with inconsistentData("imdb rating"),
                runtime = parseDuration(this.Runtime)?.let { Runtime(it) }
                    ?: return@with inconsistentData("runtime"),
                catalogedAt = config.clock.instant()
            )
                .right()
        }
    } catch (error: Throwable) {
        log.error("Error fetching movie info for IMDb id: $imdbId", error)
        error.left()
    }
}

private fun inconsistentData(dataName: String): Either<Throwable, NewMovie> =
    InconsistentOMDbData(dataName).left()

private val OMDbLocalDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

private fun parseDate(string: String): LocalDate? =
    try {
        LocalDate.parse(string, OMDbLocalDateFormatter)
    } catch (error: Throwable) {
        null
    }

private fun parseBigInt(string: String): Int? =
    string.replace(",", "").toIntOrNull()

private fun parseDuration(string: String): Duration? =
    string.split(" ")[0]
        .toLongOrNull()
        ?.let(Duration::ofMinutes)

private data class MovieInfoDto(
    val Title: String,
    val Released: String,
    val Runtime: String,
    val Plot: String,
    val imdbRating: Double,
    val imdbVotes: String,
    val imdbID: String
)

private class InconsistentOMDbData(dataName: String) : RuntimeException("Inconsistent OMDb data: $dataName")

private val log = LoggerFactory.getLogger("FetchMovieInfo")
