package ru.hse.apphost

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.calllogging.CallLogging
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.hse.apphost.di.appModule
import ru.hse.apphost.routing.configureGatewayRouting

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
    
    configureGatewayRouting()
}

