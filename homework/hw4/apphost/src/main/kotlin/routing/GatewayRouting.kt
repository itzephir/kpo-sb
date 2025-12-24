package ru.hse.apphost.routing

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.configureGatewayRouting() {
    routing {
        val ordersClient: HttpClient by inject(qualifier = named("ordersClient"))
        val paymentsClient: HttpClient by inject(qualifier = named("paymentsClient"))
        val ordersServiceUrl: String by inject(qualifier = named("ordersServiceUrl"))
        val paymentsServiceUrl: String by inject(qualifier = named("paymentsServiceUrl"))
        
        // Payments Service routes
        route("/accounts") {
            post {
                proxyRequest(paymentsClient, paymentsServiceUrl, "/accounts", call)
            }
            
            post("/topup") {
                proxyRequest(paymentsClient, paymentsServiceUrl, "/accounts/topup", call)
            }
            
            get("/balance") {
                proxyRequest(paymentsClient, paymentsServiceUrl, "/accounts/balance", call)
            }
        }
        
        // Orders Service routes
        route("/orders") {
            post {
                proxyRequest(ordersClient, ordersServiceUrl, "/orders", call)
            }
            
            get {
                proxyRequest(ordersClient, ordersServiceUrl, "/orders", call)
            }
            
            get("/{id}") {
                val orderId = call.parameters["id"]
                proxyRequest(ordersClient, ordersServiceUrl, "/orders/$orderId", call)
            }
        }
    }
}

private suspend fun proxyRequest(
    client: HttpClient,
    baseUrl: String,
    path: String,
    call: ApplicationCall
) {
    try {
        val targetUrl = "$baseUrl$path${call.request.queryString().let { if (it.isNotEmpty()) "?$it" else "" }}"
        
        val response: HttpResponse = when (call.request.httpMethod) {
            HttpMethod.Get -> {
                client.get(targetUrl) {
                    headers {
                        // Copy all headers from original request
                        call.request.headers.forEach { name, values ->
                            values.forEach { value ->
                                append(name, value)
                            }
                        }
                    }
                }
            }
            HttpMethod.Post -> {
                val body = try {
                    call.receiveText()
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
                client.post(targetUrl) {
                    headers {
                        call.request.headers.forEach { name, values ->
                            values.forEach { value ->
                                append(name, value)
                            }
                        }
                    }
                    setBody(body)
                }
            }
            else -> {
                call.respond(HttpStatusCode.MethodNotAllowed, mapOf("error" to "Method not supported"))
                return
            }
        }
        
        // Copy response headers
        response.headers.forEach { name, values ->
            values.forEach { value ->
                call.response.headers.append(name, value)
            }
        }
        
        // Copy response status and body
        call.respond(response.status, response.bodyAsText())
    } catch (e: Exception) {
        call.respond(
            HttpStatusCode.InternalServerError,
            mapOf("error" to "Failed to proxy request: ${e.message}")
        )
    }
}

