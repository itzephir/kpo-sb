package ru.hse.apphost

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.apache.logging.log4j.kotlin.logger
import ru.hse.apphost.di.configureDI
import ru.hse.apphost.routing.configureRouting
import ru.hse.apphost.routing.configureRpcRouting
import ru.hse.apphost.rpc.configureRpc
import ru.hse.apphost.serialization.configureSerialization

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    println("Starting apphost")
    configureLogging()
    println("Starting apphost")
    configureDI()
    println("Starting apphost")
    configureRpc()
    println("Starting apphost")
    configureSerialization()
    println("Starting apphost")
    configureRpcRouting()
    println("Starting apphost")
    configureRouting()
}
