package ru.hsebank.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    override val id: Id = Id.random,
    val type: Type,
    val name: Name,
) : Identifiable
