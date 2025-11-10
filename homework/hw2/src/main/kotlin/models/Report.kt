package ru.hsebank.models

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Report(
    val date: Instant,
    val categories: List<Category>,
    val accountsAndOperations: Map<Account, List<Operation>>,
)
