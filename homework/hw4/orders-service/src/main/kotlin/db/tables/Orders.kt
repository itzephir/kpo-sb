package ru.hse.orders.db.tables

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.Clock

object Orders : UUIDTable("orders") {
    val userId = varchar("user_id", 255).index()
    val amount = decimal("amount", 10, 2)
    val description = text("description").nullable()
    val status = varchar("status", 50).default("NEW")
    val createdAt = timestamp("created_at").default(Clock.System.now())
    val updatedAt = timestamp("updated_at").default(Clock.System.now())
}

