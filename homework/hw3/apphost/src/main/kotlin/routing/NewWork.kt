package ru.hse.apphost.routing

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.serialization.Serializable
import ru.hse.apphost.serialization.json
import ru.hse.apphost.servant.NewWorkServant
import ru.hse.core.Meta

@Serializable
data class MetaRequest(
    val name: String,
    val author: String,
    val type: String,
)

fun Route.newWork(servant: NewWorkServant) = post("/work") {
    var workData: Buffer? = null
    var metaRequest: MetaRequest? = null
    call.receiveMultipart().forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                if (part.name != "work") {
                    return@forEachPart
                }
                workData = part.provider().readBuffer()
            }

            is PartData.FormItem -> {
                if (part.name != "meta") {
                    return@forEachPart
                }
                metaRequest = json.decodeFromString(part.value)
            }

            else                 -> Unit
        }
        part.dispose()
    }
    val work = workData?.readByteArray() ?: return@post call.respond(HttpStatusCode.BadRequest)
    val meta = metaRequest?.let {
        Meta(
            name = it.name,
            author = it.author,
            type = it.type,
            size = work.size.toLong(),
        )
    } ?: return@post call.respond(HttpStatusCode.BadRequest)

    println(work.decodeToString())
    val result = servant.uploadNewWork(meta, work)
    if (!result) {
        return@post call.respond(HttpStatusCode.InternalServerError)
    }
    call.respondText("Ok")
}