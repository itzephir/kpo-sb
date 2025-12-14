package ru.hse.apphost.api

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc

@Rpc
interface ApphostService {
    fun getAllWorks(): Flow<ByteArray>
}