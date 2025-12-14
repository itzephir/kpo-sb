package ru.hse.analyse

import io.ktor.server.application.*
import io.ktor.server.netty.*
import ru.hse.analyse.db.configureDatabase
import ru.hse.analyse.di.configureDI
import ru.hse.analyse.routing.configureRpcRouting
import ru.hse.analyse.rpc.configureRpc

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureLogging()
    configureDI()
    configureDatabase()
    configureRpc()
    configureRpcRouting()
}