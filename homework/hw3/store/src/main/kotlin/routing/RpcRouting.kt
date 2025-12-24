package ru.hse.store.routing

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import ru.hse.store.api.StoreService

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureRpcRouting() {
    val storeService: StoreService by dependencies
    val storeService2: StoreService by dependencies
    routing {
        rpc("/srvs"){
            rpcConfig {
                serialization {
                    protobuf()
                }
            }
            registerService { storeService2 }
        }
        rpc("/services") {
            rpcConfig {
                serialization {
                    protobuf()
                }
            }

            registerService { storeService }
        }
    }
}
