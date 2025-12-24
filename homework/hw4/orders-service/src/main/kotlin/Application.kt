package ru.hse.orders

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.calllogging.CallLogging
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.hse.orders.db.configureDatabase
import ru.hse.orders.di.appModule
import ru.hse.orders.kafka.KafkaConsumerService
import ru.hse.orders.outbox.OutboxProcessor
import ru.hse.orders.routing.configureOrdersRouting

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
    configureOrdersRouting()
    
    // Start Kafka consumers and processors
    val kafkaConsumer: KafkaConsumerService by inject()
    val outboxProcessor: OutboxProcessor by inject()
    
    kafkaConsumer.start()
    outboxProcessor.start()
}

