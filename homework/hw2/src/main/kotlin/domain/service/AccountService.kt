package ru.hsebank.domain.service

import kotlinx.coroutines.flow.Flow
import ru.hsebank.domain.repository.AccountRepository
import ru.hsebank.models.Account
import ru.hsebank.models.Id

class AccountService(
    private val accountRepository: AccountRepository,
) {
    fun allAccounts(): Flow<Account> = accountRepository.getAccounts()

    suspend fun createAccount(account: Account): Boolean {
        return accountRepository.createAccount(account)
    }

    suspend fun findAccountById(id: Id): Account? {
        return accountRepository.readAccountById(id)
    }

    suspend fun updateAccount(account: Account): Account? {
        return accountRepository.updateAccountById(account.id, account)
    }

    suspend fun deleteAccountById(id: Id): Account? {
        return accountRepository.deleteAccountById(id)
    }
}