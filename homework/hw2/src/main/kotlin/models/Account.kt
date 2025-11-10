package ru.hsebank.models

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    override val id: Id = Id.random,
    val name: Name,
    val balance: Amount,
) : Identifiable
