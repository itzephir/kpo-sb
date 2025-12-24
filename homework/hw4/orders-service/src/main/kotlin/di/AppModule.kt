package ru.hse.orders.di

import io.ktor.server.application.Application
import org.koin.dsl.module
import ru.hse.orders.kafka.KafkaConsumerService
import ru.hse.orders.kafka.KafkaProducerService
import ru.hse.orders.model.OrderStatus
import ru.hse.orders.outbox.OutboxProcessor
import ru.hse.orders.service.OrderService

fun Application.appModule() = module {
    single { OrderService() }
    
    single {
        val bootstrapServers = environment.config.property("kafka.bootstrapServers").getString()
        val paymentRequestTopic = environment.config.property("kafka.topics.paymentRequest").getString()
        KafkaProducerService(bootstrapServers, paymentRequestTopic)
    }
    
    single {
        val bootstrapServers = environment.config.property("kafka.bootstrapServers").getString()
        val paymentStatusTopic = environment.config.property("kafka.topics.paymentStatus").getString()
        val orderService = get<OrderService>()
        KafkaConsumerService(
            bootstrapServers,
            "orders-service-group",
            paymentStatusTopic
        ) { _, value ->
            // Handle payment status messages
            try {
                val statusEvent = kotlinx.serialization.json.Json.decodeFromString<ru.hse.core.events.PaymentStatusEvent>(value)
                when (statusEvent.status) {
                    "SUCCESS" -> orderService.updateOrderStatus(statusEvent.orderId, OrderStatus.FINISHED)
                    "FAILED" -> orderService.updateOrderStatus(statusEvent.orderId, OrderStatus.CANCELLED)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    single {
        OutboxProcessor(get())
    }
}

