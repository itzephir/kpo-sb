package ru.hse.store.api

import kotlinx.rpc.annotations.Rpc
import ru.hse.core.Meta
import kotlin.uuid.Uuid

@Rpc
interface StoreService {
    suspend fun save(meta: Meta, bytes: ByteArray): Boolean

    suspend fun getById(id: Uuid): ByteArray?

    suspend fun getByIds(ids: List<Uuid>): List<ByteArray>

    suspend fun getMetaByNameAndAuthor(name: String, author: String): Meta?

    suspend fun getAllMetasByName(name: String): List<Meta>

    suspend fun getAllMetas(): List<Meta>

    suspend fun deleteById(id: Uuid): ByteArray?
}