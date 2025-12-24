package ru.hse.apphost.routing

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.hse.apphost.servant.GetWorkServant
import ru.hse.apphost.servant.GetWorksServant

fun Route.getWork(servant: GetWorkServant) = get("/work/{work_id}") {
    val workId = call.parameters["work_id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
    val work = servant.getWork(workId) ?: return@get call.respond(HttpStatusCode.NotFound)
    call.respondBytes(work)
}

fun Route.getWorks(servant: GetWorksServant) = get("/works") {
    val works = servant.getWorks()
    call.respond(works)
}
