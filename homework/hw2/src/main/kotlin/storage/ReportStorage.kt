package ru.hsebank.storage

import ru.hsebank.models.Report
import kotlin.time.Instant

interface ReportStorage {
    suspend fun writeReport(report: Report): Boolean

    suspend fun readReport(date: Instant): Report?
}