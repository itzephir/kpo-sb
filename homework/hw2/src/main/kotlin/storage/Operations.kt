package ru.hsebank.storage

import ru.hsebank.models.Id
import ru.hsebank.models.Operation

class OperationsVisitor : StorageVisitor<Operation> {
    val accountIdToOperations = mutableMapOf<Id, List<Operation>>()

    override fun visitAdd(value: Operation?) {
        super.visitAdd(value)
        val operation = value ?: return
        if (operation.bankAccountId in accountIdToOperations) {
            accountIdToOperations[operation.bankAccountId] =
                accountIdToOperations.getValue(operation.bankAccountId) + operation
        } else {
            accountIdToOperations[operation.bankAccountId] = listOf(operation)
        }
    }

    override fun visitUpdate(oldValue: Operation?, newValue: Operation?) {
        super.visitUpdate(oldValue, newValue)
        val (oldOperation, newOperation) = (oldValue ?: return) to (newValue ?: return)
        if (oldOperation.bankAccountId != newOperation.bankAccountId) {
            accountIdToOperations[oldOperation.bankAccountId] =
                accountIdToOperations.getValue(oldOperation.bankAccountId) - oldOperation
            if (newOperation.bankAccountId in accountIdToOperations) {
                accountIdToOperations[newOperation.bankAccountId] =
                    accountIdToOperations.getValue(newOperation.bankAccountId) + newOperation
                return
            }
            accountIdToOperations[newOperation.bankAccountId] = listOf(newOperation)
            return
        }
        accountIdToOperations[oldOperation.bankAccountId] =
            accountIdToOperations.getValue(oldOperation.bankAccountId) - oldOperation + newOperation
    }

    override fun visitRemove(value: Operation?) {
        super.visitRemove(value)
        val operation = value ?: return
        accountIdToOperations[operation.bankAccountId] =
            accountIdToOperations.getValue(operation.bankAccountId) - operation
    }
}

class Operations(operationsVisitors: List<OperationsVisitor>) : StorageVisitable<Operation>(operationsVisitors)
