package ru.hse.apphost.servant

import ru.hse.analyse.api.AnalyseService
import ru.hse.analyse.api.Report
import kotlin.uuid.Uuid

class GetWorkReportServant(private val analyseService: AnalyseService) {
    suspend fun getWorkReport(workId: String): Report? =
        analyseService.getById(Uuid.parse(workId))
}