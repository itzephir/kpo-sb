package ru.hse.orders.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val amount: String,
    val description: String? = null
)

@Serializable
data class OrderResponse(
    val id: String,
    val userId: String,
    val amount: String,
    val description: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

enum class OrderStatus {
    NEW,
    FINISHED,
    CANCELLED
}

