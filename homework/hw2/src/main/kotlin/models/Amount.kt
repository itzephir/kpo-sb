package ru.hsebank.models

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Amount(
    val value: Long,
) {
    fun asDouble() = value.toDouble() / 100

    override fun toString(): String = asDouble().toString()
}