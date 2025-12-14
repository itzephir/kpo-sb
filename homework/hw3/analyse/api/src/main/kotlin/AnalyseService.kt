package ru.hse.analyse.api

import kotlinx.rpc.annotations.Rpc
import ru.hse.core.Meta
import kotlin.uuid.Uuid

@Rpc
interface AnalyseService {
    suspend fun analyse(meta: Meta, bytes: ByteArray): Boolean

    suspend fun getById(id: Uuid): Report?

}