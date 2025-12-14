package ru.hse.apphost.routing

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.hse.apphost.servant.GetWorkReportServant

fun Route.getWorkReport(servant: GetWorkReportServant) = get("/work/{work_id}/report") {
    val workId = call.parameters["work_id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
    val work = servant.getWorkReport(workId) ?: return@get call.respond(HttpStatusCode.NotFound)
    call.respond(work)
}