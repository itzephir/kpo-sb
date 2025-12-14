package ru.hse.core

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Meta(
    val uuid: Uuid = Uuid.random(),
    val name: String,
    val author: String,
    val type: String,
    val size: Long,
)