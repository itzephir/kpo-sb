package ru.hse.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.ExperimentalExtendedContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
inline fun <T> makeIf(value: Boolean, constructor: () -> T): T? {
    contract {
        value holdsIn constructor
        returnsNotNull() implies value
    }
    return if (value) constructor() else null
}
