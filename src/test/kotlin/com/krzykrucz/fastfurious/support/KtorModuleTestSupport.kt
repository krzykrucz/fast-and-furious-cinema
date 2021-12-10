package com.krzykrucz.fastfurious.support

import com.krzykrucz.fastfurious.eventbus.IntegrationEvent
import io.kotest.assertions.timing.eventually
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.isRootTest
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainInOrder
import io.ktor.application.Application
import io.ktor.application.EventDefinition
import java.util.Collections
import kotlin.time.Duration.Companion.seconds

class KtorModuleTestSupport(testedModule: Application.() -> Unit) : TestListener {
    private val consumedEvents: MutableList<IntegrationEvent> = Collections.synchronizedList(mutableListOf())

    val moduleTestApp: Application.() -> Unit = {
        listenTo(IntegrationEvent.MovieAddedToCatalogEvent)
        listenTo(IntegrationEvent.MovieRatedEvent)
        testedModule()
    }

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
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
            consumedEvents.shouldContainInOrder(events)
        }
    }
}