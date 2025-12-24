package ru.hse.apphost.di

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.appModule() = module {
    val ordersServiceUrl = environment.config.property("services.orders.url").getString()
    val paymentsServiceUrl = environment.config.property("services.payments.url").getString()
    
    single(named("ordersClient")) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
            engine {
                config {
                    followRedirects(true)
                }
            }
        }
    }
    
    single(named("paymentsClient")) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
            engine {
                config {
                    followRedirects(true)
                }
            }
        }
    }
    
    single(named("ordersServiceUrl")) { ordersServiceUrl }
    single(named("paymentsServiceUrl")) { paymentsServiceUrl }
}

