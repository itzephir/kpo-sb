package ru.hse.apphost.di

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.rpc.RpcClient
import kotlinx.rpc.withService
import ru.hse.analyse.api.AnalyseService
import ru.hse.apphost.api.ApphostService
import ru.hse.apphost.rpc.rpcClient
import ru.hse.apphost.servant.GetWorkReportServant
import ru.hse.apphost.servant.GetWorkServant
import ru.hse.apphost.servant.GetWorksServant
import ru.hse.apphost.servant.NewWorkServant
import ru.hse.apphost.service.ApphostServiceImpl
import ru.hse.store.api.StoreService

fun Application.configureDI(): Unit {
    val analyseClient = rpcClient(environment.config.property("rpc.analyse.url").getString())
    val storeClient = rpcClient(environment.config.property("rpc.store.url").getString())
    dependencies {
        provide(::NewWorkServant)
        provide(::GetWorkServant)
        provide(::GetWorkReportServant)
        provide(::GetWorksServant)
        provide<ApphostService> { ApphostServiceImpl(get(DependencyKey<StoreService>())) }

        provide("analyse") { analyseClient }
        provide("store") { storeClient }

        provide { get<RpcClient>(DependencyKey<RpcClient>(name = "analyse")).withService<AnalyseService>() }
        provide { get<RpcClient>(DependencyKey<RpcClient>(name = "store")).withService<StoreService>() }
    }
}
