package ru.hse.analyse.db

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import ru.hse.core.kotlinUuid
import java.util.UUID
import kotlin.uuid.Uuid


object Reports : IdTable<UUID>("reports") {
    override val id: Column<EntityID<UUID>> = uuid("id").autoGenerate().entityId()
    val workId: Column<UUID> = uuid("work_id")
    val author = text("author")
    val plagiarismPercentage = double("plagiarism_percentage")
    val isPlagiarism = bool("is_plagiarism")
}