package ru.hsebank.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.hsebank.models.Account
import ru.hsebank.models.Id

interface AccountRepository {
    fun getAccounts(): Flow<Account>

    suspend fun createAccount(account: Account): Boolean
    suspend fun readAccountById(id: Id): Account?
    suspend fun updateAccountById(id: Id, account: Account): Account?
    suspend fun deleteAccountById(id: Id): Account?
}