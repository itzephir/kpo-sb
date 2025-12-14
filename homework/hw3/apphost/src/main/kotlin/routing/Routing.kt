package ru.hse.apphost.routing

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import ru.hse.apphost.servant.GetWorkReportServant
import ru.hse.apphost.servant.GetWorkServant
import ru.hse.apphost.servant.GetWorksServant
import ru.hse.apphost.servant.NewWorkServant

fun Application.configureRouting() {
    val newWorkServant: NewWorkServant by dependencies
    val getWorkServant: GetWorkServant by dependencies
    val getWorkReportServant: GetWorkReportServant by dependencies
    val getWorksServant: GetWorksServant by dependencies
    routing {
        newWork(newWorkServant)
        getWork(getWorkServant)
        getWorkReport(getWorkReportServant)
        getWorks(getWorksServant)
    }
}