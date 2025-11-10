package ru.hsebank.data.json

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.json.Json
import ru.hsebank.data.ReportRepository
import ru.hsebank.models.Report

class JsonReportRepository(
    val json: Json,
) : ReportRepository {
    override suspend fun read(source: Source): Report = source.use {
        return json.decodeFromString(it.readString())
    }

    override suspend fun write(sink: Sink, data: Report) = sink.use {
        it.writeString(json.encodeToString(data))
    }

    override val format: String = "json"
}