package ru.hsebank.models

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Name(
    val value: String,
){
    override fun toString(): String = value
}