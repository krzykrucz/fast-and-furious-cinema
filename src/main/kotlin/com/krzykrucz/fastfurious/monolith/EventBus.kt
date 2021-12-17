package com.krzykrucz.fastfurious.monolith

import arrow.core.Either
import arrow.core.left
import arrow.core.right
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
                log.error("Error handling event $definition with id: ${event.id}", error)
            }
        }
    }

suspend fun <T : IntegrationEvent> ApplicationEvents.publishAsync(
    definition: EventDefinition<T>,
    event: T
): Either<Throwable, Unit> =
    try {
        coroutineScope {
            launch(IO) {
                raise(definition, event)
            }
        }
        Unit.right()
    } catch (error: Throwable) {
        log.error("Error publishing event $definition with id: ${event.id}", error)
        error.left()
    }

private val log = LoggerFactory.getLogger("EventBus")