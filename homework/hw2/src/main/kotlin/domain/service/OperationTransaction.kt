package ru.hsebank.domain.service

import kotlinx.coroutines.flow.Flow
import ru.hsebank.domain.repository.AccountRepository
import ru.hsebank.domain.repository.OperationRepository
import ru.hsebank.models.Account
import ru.hsebank.models.Amount
import ru.hsebank.models.Id
import ru.hsebank.models.Operation

class OperationTransactionManager(
    val operationRepository: OperationRepository,
    val accountRepository: AccountRepository,
) : OperationRepository {
    override fun getOperations(): Flow<Operation> {
        return operationRepository.getOperations()
    }

    override suspend fun createOperation(operation: Operation): Boolean {
        val account = accountRepository.readAccountById(operation.bankAccountId) ?: return false
        if (!operationRepository.createOperation(operation)) return false
        val newAccount = account.copy(balance = Amount(account.balance.value + operation.difference))
        accountRepository.updateAccountById(account.id, newAccount) ?: run {
            operationRepository.deleteOperationById(operation.id)
            return false
        }
        return true
    }

    override fun getOperationsByAccount(): Flow<Pair<Account, List<Operation>>> {
        return operationRepository.getOperationsByAccount()
    }

    override suspend fun readOperationById(id: Id): Operation? {
        return readOperationById(id)
    }

    override suspend fun updateOperationById(
        id: Id,
        operation: Operation,
    ): Operation? {
        val oldOperation = operationRepository.updateOperationById(id, operation) ?: return null
        if (oldOperation.bankAccountId != operation.bankAccountId) {
            val newAccount = accountRepository.readAccountById(operation.bankAccountId) ?: run {
                operationRepository.updateOperationById(id, oldOperation)
                return null
            }
            val oldAccount = accountRepository.readAccountById(oldOperation.bankAccountId) ?: run {
                operationRepository.updateOperationById(id, oldOperation)
                return null
            }
            val newAccountNew = newAccount.copy(balance = Amount(newAccount.balance.value + operation.difference))
            val oldAccountNew = oldAccount.copy(balance = Amount(oldAccount.balance.value - oldOperation.difference))
            accountRepository.updateAccountById(newAccount.id, newAccountNew) ?: run {
                operationRepository.updateOperationById(id, oldOperation)
                return null
            }
            accountRepository.updateAccountById(oldAccount.id, oldAccountNew) ?: run {
                operationRepository.updateOperationById(id, oldOperation)
                accountRepository.updateAccountById(newAccount.id, newAccount)
                return null
            }
            return oldOperation
        }
        val account = accountRepository.readAccountById(oldOperation.bankAccountId) ?: run {
            operationRepository.updateOperationById(id, oldOperation)
            return null
        }
        val newAccount =
            account.copy(balance = Amount(account.balance.value + operation.difference - oldOperation.difference))
        accountRepository.updateAccountById(account.id, newAccount) ?: run {
            operationRepository.updateOperationById(id, oldOperation)
            return null
        }
        return oldOperation
    }

    override suspend fun deleteOperationById(id: Id): Operation? {
        val operation = operationRepository.deleteOperationById(id) ?: return null
        val account = accountRepository.readAccountById(operation.bankAccountId) ?: run {
            operationRepository.createOperation(operation)
            return null
        }
        val newAccount = account.copy(balance = Amount(account.balance.value - operation.difference))
        accountRepository.updateAccountById(account.id, newAccount) ?: run {
            operationRepository.createOperation(operation)
            return null
        }
        return operation
    }
}