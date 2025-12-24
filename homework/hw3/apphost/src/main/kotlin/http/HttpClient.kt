package ru.hse.apphost.http

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.rpc.krpc.ktor.client.Krpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
val httpClient: HttpClient = HttpClient(OkHttp) {
    install(WebSockets)

    install(Krpc) {
        serialization {
            json()
        }
    }
}