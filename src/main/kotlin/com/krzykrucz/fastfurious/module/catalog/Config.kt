package com.krzykrucz.fastfurious.module.catalog

import com.krzykrucz.fastfurious.monolith.EnvironmentName
import java.time.Clock

val catalogConfig: Map<EnvironmentName, CatalogConfig>
    get() = mapOf(
        EnvironmentName.LOCAL to catalogConfig {
            omdbApiKey = System.getenv("OMDB_API_KEY")
            omdbBaseUrl = "http://www.omdbapi.com"
        }
    )

data class CatalogConfigBuilder(
    var clock: Clock = Clock.systemUTC(),
    var omdbApiKey: String? = null,
    var omdbBaseUrl: String? = null,
    var omdbRetries: Int = 0
) {
    fun build(): CatalogConfig =
        CatalogConfig(clock, omdbApiKey!!, omdbBaseUrl!!, omdbRetries)
}

data class CatalogConfig(
    val clock: Clock,
    val omdbApiKey: String,
    val omdbBaseUrl: String,
    val omdbRetries: Int
)

fun catalogConfig(init: CatalogConfigBuilder.() -> Unit): CatalogConfig =
    CatalogConfigBuilder().apply(init).build()