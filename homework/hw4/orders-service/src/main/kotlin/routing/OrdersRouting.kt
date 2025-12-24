package ru.hse.orders.routing

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.hse.orders.model.CreateOrderRequest
import ru.hse.orders.service.OrderService

fun Application.configureOrdersRouting() {
    routing {
        val orderService: OrderService by inject()
        
        route("/orders") {
            post {
                val userId = call.request.header("X-User-Id")
                    ?: return@post call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        mapOf("error" to "X-User-Id header is required")
                    )
                
                val request = call.receive<CreateOrderRequest>()
                val orderId = orderService.createOrder(userId, request.amount, request.description)
                
                call.respond(
                    mapOf(
                        "id" to orderId,
                        "status" to "NEW"
                    )
                )
            }
            
            get {
                val userId = call.request.header("X-User-Id")
                    ?: return@get call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        mapOf("error" to "X-User-Id header is required")
                    )
                
                val orders = orderService.getOrders(userId)
                call.respond(orders)
            }
            
            get("/{id}") {
                val userId = call.request.header("X-User-Id")
                    ?: return@get call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        mapOf("error" to "X-User-Id header is required")
                    )
                
                val orderId = call.parameters["id"]
                    ?: return@get call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        mapOf("error" to "Order ID is required")
                    )
                
                val order = orderService.getOrder(orderId, userId)
                    ?: return@get call.respond(
                        io.ktor.http.HttpStatusCode.NotFound,
                        mapOf("error" to "Order not found")
                    )
                
                call.respond(order)
            }
        }
    }
}

