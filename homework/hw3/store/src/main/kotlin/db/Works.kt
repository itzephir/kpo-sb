package ru.hse.store.db

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import java.util.*

object Works : IdTable<UUID>("works") {
    override val id: Column<EntityID<UUID>> = uuid("id").autoGenerate().entityId()
    val name = text("name")
    val author = text("author")
    val type = text("type")
    val size = long("size")
}