package ru.hsebank.domain.service

import kotlinx.coroutines.flow.toList
import ru.hsebank.builder.ReportBuilder
import ru.hsebank.domain.repository.CategoryRepository
import ru.hsebank.domain.repository.OperationRepository
import ru.hsebank.models.Report
import ru.hsebank.storage.LocalReportStorage
import kotlin.time.Instant

class ReportService(
    private val categoryRepository: CategoryRepository,
    private val operationRepository: OperationRepository,
    private val localReportStorage: LocalReportStorage,
) {
    suspend fun generateReport(date: Instant? = null): Report {
        return if (date != null) {
            ReportBuilder(date)
        } else {
            ReportBuilder()
        }
            .withAccountsAndOperations(operationRepository.getOperationsByAccount().toList().toMap())
            .withCategories(categoryRepository.getCategories())
            .build()
    }

    suspend fun loadReport(date: Instant): Report? {
        return localReportStorage.readReport(date)
    }

    suspend fun saveReport(report: Report): Boolean {
        return localReportStorage.writeReport(report)
    }
}