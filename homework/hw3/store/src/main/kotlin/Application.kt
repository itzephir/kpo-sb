package ru.hse.store

import io.ktor.server.application.*
import io.ktor.server.netty.*
import ru.hse.store.db.configureDatabase
import ru.hse.store.di.configureDI
import ru.hse.store.routing.configureRpcRouting
import ru.hse.store.rpc.configureRpc

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureLogging()
    configureDatabase()
    configureDI()
    configureRpc()
    configureRpcRouting()
}