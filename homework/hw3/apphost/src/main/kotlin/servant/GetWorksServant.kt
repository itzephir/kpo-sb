package ru.hse.apphost.servant

import ru.hse.core.Meta
import ru.hse.store.api.StoreService

class GetWorksServant(private val storeService: StoreService) {
    suspend fun getWorks(): List<Pair<Meta, ByteArray>> {
        val metas = storeService.getAllMetas()
        return metas.mapNotNull {
            it to (storeService.getById(it.uuid) ?: return@mapNotNull null)
        }
    }
}