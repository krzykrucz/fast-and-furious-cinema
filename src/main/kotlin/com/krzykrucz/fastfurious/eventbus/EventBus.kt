package com.krzykrucz.fastfurious.eventbus

import io.ktor.application.Application
import io.ktor.application.ApplicationEvents
import io.ktor.application.EventDefinition
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


val Application.events
    get() = environment.monitor

fun <T : IntegrationEvent> Application.subscribeOn(definition: EventDefinition<T>, handle: suspend (T) -> Unit) =
    environment.monitor.subscribe(definition) { event ->
        launch {
            try {
                handle(event)
            } catch (error: Throwable) {
                log.error("Error handling event $definition with id: ${event.id}")
            }
        }
    }

suspend fun <T : IntegrationEvent> ApplicationEvents.publishAsync(definition: EventDefinition<T>, event: T) {
    coroutineScope {
        launch(IO) {
            raise(definition, event)
        }
    }
}

private val log = LoggerFactory.getLogger("EventBus")