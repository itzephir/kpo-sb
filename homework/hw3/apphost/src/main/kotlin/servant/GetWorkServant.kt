package ru.hse.apphost.servant

import ru.hse.store.api.StoreService
import kotlin.uuid.Uuid

class GetWorkServant(private val storeService: StoreService) {
    suspend fun getWork(workId: String): ByteArray? =
        storeService.getById(Uuid.parse(workId))
}