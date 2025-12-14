package ru.hse.store.rpc

import io.ktor.server.application.*
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.serialization.json.json

fun Application.configureRpc() {
    install(Krpc){
        serialization {
            json()
        }
    }
}
