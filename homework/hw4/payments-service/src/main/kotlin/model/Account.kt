package ru.hse.payments.model

import kotlinx.serialization.Serializable

@Serializable
data class TopUpAccountRequest(
    val amount: String
)

@Serializable
data class BalanceResponse(
    val userId: String,
    val balance: String
)

