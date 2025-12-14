package ru.hse.store.services

import org.apache.logging.log4j.kotlin.logger
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteReturning
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.hse.core.Meta
import ru.hse.store.api.StoreService
import ru.hse.store.db.Works
import ru.hse.store.storage.WorkStorage
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class StoreServiceImpl(
    private val workStorage: WorkStorage,
) : StoreService {
    override suspend fun save(meta: Meta, bytes: ByteArray): Boolean = suspendTransaction {
        logger.info { "Saving $meta" }
        println(bytes.decodeToString())
        Works.insert {
            it[id] = meta.uuid.toJavaUuid()
            it[name] = meta.name
            it[author] = meta.author
            it[type] = meta.type
            it[size] = meta.size
        }
        if (!workStorage.save(meta.name, bytes)) {
            rollback()
            return@suspendTransaction false
        }
        true
    }

    override suspend fun getById(id: Uuid): ByteArray? = transaction {
        logger.info { "Getting $id" }
        println(Works.selectAll().where { Works.id eq id.toJavaUuid() }.map { it.toString() })
        Works.selectAll().where { Works.id eq id.toJavaUuid() }.singleOrNull()?.let {
            workStorage.read(it[Works.name])?.also {
                println(it.decodeToString())
            }
        }
    }

    override suspend fun getByIds(ids: List<Uuid>): List<ByteArray> = transaction {
        logger.info { "Getting $ids" }
        Works.selectAll().where { Works.id inList ids.map { it.toJavaUuid() } }
            .mapNotNull { workStorage.read(it[Works.name]) }
    }

    override suspend fun getMetaByNameAndAuthor(name: String, author: String): Meta? = transaction {
        logger.info { "Getting $name by $author" }
        Works.selectAll().where { (Works.name eq name) and (Works.author eq author) }.singleOrNull()?.toMeta()
    }

    override suspend fun getAllMetasByName(name: String): List<Meta> = transaction {
        logger.info { "Getting all $name" }
        Works.selectAll().where { Works.name eq name }.mapNotNull {
            it.toMeta()
        }
    }

    override suspend fun getAllMetas(): List<Meta> = transaction {
        logger.info { "Getting all" }
        Works.selectAll().mapNotNull {
            it.toMeta()
        }
    }

    override suspend fun deleteById(id: Uuid): ByteArray? = suspendTransaction {
        logger.info { "Deleting $id" }
        Works.deleteReturning(where = { Works.id eq id.toJavaUuid() }).let {
            workStorage.delete(it.singleOrNull()?.get(Works.name) ?: return@suspendTransaction null)
        }
    }

    private fun ResultRow.toMeta() = Meta(
        uuid = this[Works.id].value.toKotlinUuid(),
        name = this[Works.name],
        author = this[Works.author],
        type = this[Works.type],
        size = this[Works.size],
    )
}