package ru.hse.payments.di

import io.ktor.server.application.*
import org.koin.dsl.module
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.hse.core.db.tables.InboxTable
import ru.hse.payments.inbox.InboxProcessor
import ru.hse.payments.kafka.KafkaConsumerService
import ru.hse.payments.kafka.KafkaProducerService
import ru.hse.payments.outbox.OutboxProcessor
import ru.hse.payments.service.PaymentService
import java.util.UUID
import kotlin.time.Clock

fun Application.appModule() = module {
    single { PaymentService() }
    
    single {
        val bootstrapServers = environment.config.property("kafka.bootstrapServers").getString()
        val paymentStatusTopic = environment.config.property("kafka.topics.paymentStatus").getString()
        KafkaProducerService(bootstrapServers, paymentStatusTopic)
    }
    
    single {
        val bootstrapServers = environment.config.property("kafka.bootstrapServers").getString()
        val paymentRequestTopic = environment.config.property("kafka.topics.paymentRequest").getString()
        KafkaConsumerService(
            bootstrapServers,
            "payments-service-group",
            paymentRequestTopic
        ) { key, value ->
            // Handle message by storing in inbox for Transactional Inbox pattern
            try {
                transaction {
                    InboxTable.insert {
                        it[messageId] = key ?: UUID.randomUUID().toString()
                        it[eventType] = "payment-request"
                        it[payload] = value
                        it[processed] = false
                        it[createdAt] = Clock.System.now()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Message will be retried by Kafka consumer
            }
        }
    }
    
    single {
        InboxProcessor()
    }
    
    single {
        OutboxProcessor(get())
    }
}

