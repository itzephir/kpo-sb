package ru.hse.payments

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.calllogging.CallLogging
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.hse.payments.db.configureDatabase
import ru.hse.payments.di.appModule
import ru.hse.payments.inbox.InboxProcessor
import ru.hse.payments.kafka.KafkaConsumerService
import ru.hse.payments.outbox.OutboxProcessor
import ru.hse.payments.routing.configurePaymentsRouting

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    install(Koin) {
        slf4jLogger()
        modules(appModule())
    }
    
    configureDatabase()
    configurePaymentsRouting()
    
    // Start Kafka consumers and processors
    val kafkaConsumer: KafkaConsumerService by inject()
    val inboxProcessor: InboxProcessor by inject()
    val outboxProcessor: OutboxProcessor by inject()
    
    kafkaConsumer.start()
    inboxProcessor.start()
    outboxProcessor.start()
}

