package ru.hse.payments.db

import io.ktor.server.application.*
import ru.hse.core.db.configureDatabase
import ru.hse.core.db.tables.InboxTable
import ru.hse.core.db.tables.OutboxTable
import ru.hse.payments.db.tables.Accounts
import ru.hse.payments.db.tables.PaymentAttempts

fun Application.configureDatabase() {
    configureDatabase(Accounts, InboxTable, OutboxTable, PaymentAttempts)
}

