package com.krzykrucz.fastfurious.support

import com.krzykrucz.fastfurious.monolith.EnvironmentName
import com.krzykrucz.fastfurious.monolith.IntegrationEvent
import io.kotest.assertions.timing.eventually
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.isRootTest
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.ktor.application.Application
import io.ktor.application.EventDefinition
import java.util.Collections
import kotlin.time.Duration.Companion.seconds

class KtorModuleTestSupport<C>(
    testedModule: Application.(EnvironmentName, Map<EnvironmentName, C>) -> Unit,
    testConfig: C
) : TestListener {
    private val consumedEvents: MutableList<IntegrationEvent> = Collections.synchronizedList(mutableListOf())

    val moduleTestApp: Application.() -> Unit = {
        listenTo(IntegrationEvent.MovieAddedToCatalogEvent)
        listenTo(IntegrationEvent.MovieRatedEvent)
        this.testedModule(EnvironmentName.TEST, mapOf(EnvironmentName.TEST to testConfig))
    }

    override suspend fun beforeTest(testCase: TestCase) {
        if (testCase.isRootTest()) {
            consumedEvents.clear()
        }
    }

    private fun <T : IntegrationEvent> Application.listenTo(eventDefinition: EventDefinition<T>) {
        environment.monitor.subscribe(eventDefinition) {
            consumedEvents += it
        }
    }

    suspend fun eventShouldBePublished(event: IntegrationEvent) {
        eventually(5.seconds) {
            consumedEvents shouldContain event
        }
    }

    suspend fun eventsShouldBePublished(vararg events: IntegrationEvent) {
        eventually(5.seconds) {
            consumedEvents.shouldContainExactlyInAnyOrder(*events)
        }
    }
}