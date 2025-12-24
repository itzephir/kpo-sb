package ru.hse.orders.db

import io.ktor.server.application.*
import ru.hse.core.db.configureDatabase
import ru.hse.core.db.tables.OutboxTable
import ru.hse.orders.db.tables.Orders

fun Application.configureDatabase() {
    configureDatabase(Orders, OutboxTable)
}

