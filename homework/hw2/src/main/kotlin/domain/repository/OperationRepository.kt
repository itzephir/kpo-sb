package ru.hsebank.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.hsebank.models.Account
import ru.hsebank.models.Id
import ru.hsebank.models.Operation

interface OperationRepository {
    fun getOperations(): Flow<Operation>
    fun getOperationsByAccount(): Flow<Pair<Account, List<Operation>>>

    suspend fun createOperation(operation: Operation): Boolean
    suspend fun readOperationById(id: Id): Operation?
    suspend fun updateOperationById(id: Id, operation: Operation): Operation?
    suspend fun deleteOperationById(id: Id): Operation?
}