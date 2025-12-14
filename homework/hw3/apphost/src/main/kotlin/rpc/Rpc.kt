package ru.hse.apphost.rpc

import io.ktor.server.application.Application
import io.ktor.server.application.install
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.serialization.json.json

fun Application.configureRpc() {
    install(Krpc) {
        serialization {
            json()
        }
    }
}