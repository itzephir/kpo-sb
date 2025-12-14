package ru.hse.analyse.di

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.rpc.RpcClient
import kotlinx.rpc.withService
import ru.hse.analyse.analyser.Analyser
import ru.hse.analyse.api.AnalyseService
import ru.hse.analyse.rpc.rpcClient
import ru.hse.analyse.services.AnalyseServiceImpl
import ru.hse.store.api.StoreService

fun Application.configureDI() {
    val allowedPercentage = environment.config.property("plagiarism.tolerance").getString().toDouble()
    val storeClient = rpcClient(environment.config.property("rpc.store.url").getString())
    dependencies {
        provide("allowed_percentage") { allowedPercentage }
        provide { storeClient }

        provide { get<RpcClient>(DependencyKey<RpcClient>()).withService<StoreService>() }

        provide<AnalyseService> {
            AnalyseServiceImpl(
                get(DependencyKey<Analyser>()),
                get(DependencyKey<Double>("allowed_percentage")),
                get(DependencyKey<StoreService>()),
            )
        }
        provide(::Analyser)
    }
}