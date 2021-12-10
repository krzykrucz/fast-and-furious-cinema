package com.krzykrucz.fastfurious.module.ratings

import com.krzykrucz.fastfurious.support.GivenApp
import com.krzykrucz.fastfurious.eventbus.IntegrationEvent
import com.krzykrucz.fastfurious.support.KtorModuleTestSupport
import io.kotest.assertions.ktor.shouldHaveContent
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest

class RatingsSpec : BehaviorSpec({

    `Given ratings app`("") { app ->

        When("call") {
            val response = app.handleRequest(HttpMethod.Post, "/rate").response

            Then("res") {
                response shouldHaveStatus HttpStatusCode.OK
                response shouldHaveContent "Hello World!"

            }
            Then("event") {
                ktorSupport.eventShouldBePublished(IntegrationEvent.MovieRatedEvent(""))
            }


        }
    }
}) {
    override fun listeners() = listOf(ktorSupport)
    override fun isolationMode() = IsolationMode.InstancePerLeaf

}

private val ktorSupport = KtorModuleTestSupport(Application::ratingsModule)

private fun BehaviorSpec.`Given ratings app`(
    name: String,
    test: suspend BehaviorSpecGivenContainerScope.(TestApplicationEngine) -> Unit
) = GivenApp("ratings app $name", ktorSupport.moduleTestApp, test)
