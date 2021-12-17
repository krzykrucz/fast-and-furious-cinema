package com.krzykrucz.fastfurious.monolith

import io.ktor.application.ApplicationEnvironment


enum class EnvironmentName {
    TEST, LOCAL
}

val ApplicationEnvironment.name: EnvironmentName
    get() = System.getenv("ENV")
        ?.let(String::uppercase)
        ?.let(EnvironmentName::valueOf)
        ?: EnvironmentName.LOCAL