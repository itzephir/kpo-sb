package ru.hse.core.db.tables

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.Clock

object OutboxTable : UUIDTable("outbox") {
    val entityId = varchar("entity_id", 255)
    val eventType = varchar("event_type", 100)
    val payload = text("payload")
    val processed = bool("processed").default(false)
    val createdAt = timestamp("created_at").default(Clock.System.now())
    val processedAt = timestamp("processed_at").nullable()
}