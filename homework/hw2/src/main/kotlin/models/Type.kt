package ru.hsebank.models

import kotlinx.serialization.Serializable

@Serializable
enum class Type {
    @Suppress("unused")
    INCOME {
        override val sign: Int = 1
    },
    @Suppress("unused")
    EXPENSE {
        override val sign: Int = -1
    };

    abstract val sign: Int
}