package ru.hse.apphost.service

import kotlinx.coroutines.flow.*
import ru.hse.apphost.api.ApphostService
import ru.hse.store.api.StoreService

class ApphostServiceImpl(private val storeService: StoreService) : ApphostService {
    override fun getAllWorks(): Flow<ByteArray> {
        println("Getting all works")
        return flow {
            emitAll(
                storeService.getAllMetas().asFlow()
                    .mapNotNull {
                        storeService.getById(it.uuid) ?: ByteArray(0)
                    }
            )
        }
    }
}