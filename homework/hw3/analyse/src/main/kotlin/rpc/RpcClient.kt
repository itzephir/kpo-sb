package ru.hse.analyse.rpc

import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import ru.hse.analyse.http.httpClient

@OptIn(ExperimentalSerializationApi::class)
fun rpcClient(url: String): RpcClient =
    httpClient.rpc(url) {
        rpcConfig {
            serialization {
                protobuf()
            }
        }
    }

