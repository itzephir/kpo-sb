package ru.hse.payments.db.tables

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.Clock

object PaymentAttempts : UUIDTable("payment_attempts") {
    val orderId = varchar("order_id", 255).uniqueIndex()
    val userId = varchar("user_id", 255)
    val amount = decimal("amount", 10, 2)
    val status = varchar("status", 50)
    val createdAt = timestamp("created_at").default(Clock.System.now())
}

