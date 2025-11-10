package ru.hsebank.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class Operation(
    override val id: Id = Id.random,
    val type: Type,
    val bankAccountId: Id,
    val amount: Amount,
    val date: Instant = Clock.System.now(),
    val description: String,
    val categoryId: Id,
) : Identifiable {
    val difference: Long = type.sign.toLong() * amount.value
}
