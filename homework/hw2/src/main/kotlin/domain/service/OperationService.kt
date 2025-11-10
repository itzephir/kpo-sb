package ru.hsebank.domain.service

import kotlinx.coroutines.flow.Flow
import ru.hsebank.domain.repository.AccountRepository
import ru.hsebank.domain.repository.CategoryRepository
import ru.hsebank.domain.repository.OperationRepository
import ru.hsebank.models.Id
import ru.hsebank.models.Operation

class OperationService(
    private val accountRepository: AccountRepository,
    private val categoryService: CategoryRepository,
    private val operationRepository: OperationRepository,
) {
    fun allOperations(): Flow<Operation> = operationRepository.getOperations()

    suspend fun createOperation(operation: Operation): Boolean {
        if (accountRepository.readAccountById(operation.bankAccountId) == null) return false
        val category = categoryService.readCategoryById(operation.categoryId) ?: return false
        if (category.type != operation.type) return false
        return operationRepository.createOperation(operation)
    }

    suspend fun findOperationById(id: Id): Operation? {
        return operationRepository.readOperationById(id)
    }

    suspend fun updateOperation(operation: Operation): Operation? {
        return operationRepository.updateOperationById(operation.id, operation)
    }

    suspend fun deleteOperationById(id: Id): Operation? {
        return operationRepository.deleteOperationById(id)
    }
}