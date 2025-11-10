package ru.hsebank.storage

import kotlinx.coroutines.CancellationException
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import ru.hsebank.data.ReportRepository
import ru.hsebank.models.Report
import kotlin.time.Instant

class LocalReportStorage(
    private val repository: ReportRepository,
    private val fileSystem: FileSystem,
) : ReportStorage {
    override suspend fun writeReport(report: Report): Boolean {
        return try {
            if (!fileSystem.exists(Path("reports"))) {
                fileSystem.createDirectories(Path("reports"))
            }
            val path = Path("reports/${report.date}.${repository.format}")
            fileSystem.sink(path).buffered().use { sink ->
                repository.write(sink, report)
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun readReport(date: Instant): Report? {
        return try {
            fileSystem.source(Path("reports/$date.${repository.format}")).buffered().use { source ->
                repository.read(source)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }
}