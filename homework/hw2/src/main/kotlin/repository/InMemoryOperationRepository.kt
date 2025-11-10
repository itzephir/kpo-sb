package ru.hsebank.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import ru.hsebank.domain.repository.AccountRepository
import ru.hsebank.domain.repository.OperationRepository
import ru.hsebank.models.Account
import ru.hsebank.models.Id
import ru.hsebank.models.Operation
import ru.hsebank.storage.Operations
import ru.hsebank.storage.OperationsVisitor

class InMemoryOperationRepository(
    private val operations: Operations,
    private val operationsVisitor: OperationsVisitor,
    private val accountRepository: AccountRepository,
) : OperationRepository {
    override fun getOperations(): Flow<Operation> = operations.values.values.asFlow()

    override fun getOperationsByAccount(): Flow<Pair<Account, List<Operation>>> = flow {
        operationsVisitor.accountIdToOperations.forEach { (accountId, operations) ->
            val account = accountRepository.readAccountById(accountId) ?: return@forEach
            emit(account to operations)
        }
    }

    override suspend fun createOperation(operation: Operation): Boolean =
        operations.add(operation)

    override suspend fun readOperationById(id: Id): Operation? =
        operations.get(id)

    override suspend fun updateOperationById(id: Id, operation: Operation): Operation? =
        operations.update(id, operation)

    override suspend fun deleteOperationById(id: Id): Operation? =
        operations.remove(id)
}