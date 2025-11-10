package ru.hsebank.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.hsebank.domain.repository.AccountRepository
import ru.hsebank.models.Account
import ru.hsebank.models.Id
import ru.hsebank.storage.Accounts
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class InMemoryAccountRepository(
    private val accounts: Accounts,
) : AccountRepository {
    override fun getAccounts(): Flow<Account> =
        accounts.values.values.asFlow()

    override suspend fun createAccount(account: Account): Boolean =
        suspendCoroutine { continuation ->
            continuation.resume(accounts.add(account))
        }

    override suspend fun readAccountById(id: Id): Account? =
        suspendCoroutine { continuation ->
            continuation.resume(accounts.get(id))
        }

    override suspend fun updateAccountById(id: Id, account: Account): Account? =
        suspendCoroutine { continuation ->
            continuation.resume(accounts.update(id, account))
        }

    override suspend fun deleteAccountById(id: Id): Account? =
        suspendCoroutine { continuation ->
            continuation.resume(accounts.remove(id))
        }
}