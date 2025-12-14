package ru.hse.analyse.api

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Report(
    val id: Uuid,
    val workId: Uuid,
    val author: String,
    val plagiarismPercentage: Double,
    val isPlagiarism: Boolean,
)