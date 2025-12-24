package ru.hse.payments.routing

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.hse.payments.model.BalanceResponse
import ru.hse.payments.model.TopUpAccountRequest
import ru.hse.payments.service.PaymentService

fun Application.configurePaymentsRouting() {
    routing {
        val paymentService: PaymentService by inject()
        
        route("/accounts") {
            post {
                val userId = call.request.header("X-User-Id")
                    ?: return@post call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        mapOf("error" to "X-User-Id header is required")
                    )
                
                val accountId = paymentService.createAccount(userId)
                call.respond(
                    mapOf(
                        "id" to accountId,
                        "userId" to userId,
                        "balance" to "0"
                    )
                )
            }
            
            post("/topup") {
                val userId = call.request.header("X-User-Id")
                    ?: return@post call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        mapOf("error" to "X-User-Id header is required")
                    )
                
                val request = call.receive<TopUpAccountRequest>()
                val success = paymentService.topupAccount(userId, request.amount)
                
                if (!success) {
                    return@post call.respond(
                        io.ktor.http.HttpStatusCode.NotFound,
                        mapOf("error" to "Account not found")
                    )
                }
                
                val balance = paymentService.getBalance(userId)
                call.respond(
                    mapOf(
                        "userId" to userId,
                        "balance" to balance.toString()
                    )
                )
            }
            
            get("/balance") {
                val userId = call.request.header("X-User-Id")
                    ?: return@get call.respond(
                        io.ktor.http.HttpStatusCode.BadRequest,
                        mapOf("error" to "X-User-Id header is required")
                    )
                
                val balance = paymentService.getBalance(userId)
                    ?: return@get call.respond(
                        io.ktor.http.HttpStatusCode.NotFound,
                        mapOf("error" to "Account not found")
                    )
                
                call.respond(BalanceResponse(userId = userId, balance = balance.toString()))
            }
        }
    }
}

