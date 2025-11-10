package ru.hsebank.storage

import ru.hsebank.models.Account

class AccountsVisitor : StorageVisitor<Account>

class Accounts(accountsVisitors: List<AccountsVisitor>) : StorageVisitable<Account>(accountsVisitors)
