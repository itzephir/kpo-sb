package ru.hse.analyse.services

import org.apache.logging.log4j.kotlin.logger
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.hse.analyse.analyser.Analyser
import ru.hse.analyse.api.AnalyseService
import ru.hse.analyse.api.Report
import ru.hse.analyse.db.Reports
import ru.hse.core.Meta
import ru.hse.store.api.StoreService
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class AnalyseServiceImpl(
    private val analyser: Analyser,
    private val maximumPercentage: Double,
    private val storeService: StoreService,
) : AnalyseService {

    override suspend fun analyse(meta: Meta, bytes: ByteArray): Boolean = suspendTransaction {
        logger.info { "Analyzing $meta" }
        val works = storeService.getAllMetas().mapNotNull {
            storeService.getById(it.uuid) ?: return@mapNotNull null
        }
        val percentage = analyser.analyse(bytes, works)
        Report(
            Uuid.random(),
            meta.uuid,
            meta.author,
            percentage,
            percentage >= maximumPercentage,
        ).let { report ->
            Reports.insert {
                it[id] = report.id.toJavaUuid()
                it[workId] = report.workId.toJavaUuid()
                it[author] = report.author
                it[isPlagiarism] = report.isPlagiarism
                it[plagiarismPercentage] = report.plagiarismPercentage
            }
        }
        true
    }

    override suspend fun getById(id: Uuid): Report? = transaction {
        logger.info { "Getting $id" }
        println(Reports.selectAll().map { it.toString() })
        Reports.selectAll().where {
            Reports.workId eq id.toJavaUuid()
        }.singleOrNull()?.let {
            println("$it")
            Report(
                id = it[Reports.id].value.toKotlinUuid(),
                workId = it[Reports.workId].toKotlinUuid(),
                author = it[Reports.author],
                plagiarismPercentage = it[Reports.plagiarismPercentage],
                isPlagiarism = it[Reports.isPlagiarism],
            )
        }
    }
}