package ru.hse.payments.db.tables

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.Clock

object Accounts : UUIDTable("accounts") {
    val userId = varchar("user_id", 255).uniqueIndex()
    val balance = decimal("balance", 10, 2).default(0.toBigDecimal())
    val version = long("version").default(0L)
    val createdAt = timestamp("created_at").default(Clock.System.now())
    val updatedAt = timestamp("updated_at").default(Clock.System.now())
}

