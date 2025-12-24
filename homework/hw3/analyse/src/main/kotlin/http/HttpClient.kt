package ru.hse.analyse.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
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