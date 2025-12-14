package ru.hse.analyse.routing

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import ru.hse.analyse.api.AnalyseService

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureRpcRouting() {
    val analyseService: AnalyseService by dependencies
    routing {
        rpc("/services") {
            rpcConfig {
                serialization {
                    protobuf()
                }
            }

            registerService { analyseService }
        }
    }
}
