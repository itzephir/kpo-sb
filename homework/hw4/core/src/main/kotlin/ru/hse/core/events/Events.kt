package ru.hse.core.events

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequestEvent(
    val orderId: String,
    val userId: String,
    val amount: String
)

@Serializable
data class PaymentStatusEvent(
    val orderId: String,
    val status: String
)