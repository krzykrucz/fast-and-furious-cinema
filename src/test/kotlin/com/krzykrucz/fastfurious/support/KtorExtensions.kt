package com.krzykrucz.fastfurious.support

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.ktor.application.Application
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment



fun BehaviorSpec.GivenApp(
    name: String,
    appModule: Application.()->Unit,
    test: suspend BehaviorSpecGivenContainerScope.(TestApplicationEngine) -> Unit
) {
    Given(name) {
        withTestApplicationAsync(appModule) {
            this@Given.test(this)
        }
    }
}

suspend fun <R> withTestApplicationAsync(
    moduleFunction: Application.() -> Unit,
    test: suspend TestApplicationEngine.() -> R
): R {
    return withApplicationAsync(createTestEnvironment()) {
        moduleFunction(application)
        test()
    }
}

private suspend fun <R> withApplicationAsync(
    environment: ApplicationEngineEnvironment = createTestEnvironment(),
    configure: TestApplicationEngine.Configuration.() -> Unit = {},
    test: suspend TestApplicationEngine.() -> R
): R {
    val engine = TestApplicationEngine(environment, configure)
    engine.start()
    try {
        return engine.test()
    } finally {
        engine.stop(0L, 0L)
    }
}