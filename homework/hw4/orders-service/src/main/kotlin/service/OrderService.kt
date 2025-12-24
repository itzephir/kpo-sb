package ru.hse.orders.service

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.hse.orders.db.tables.Orders
import ru.hse.core.db.tables.OutboxTable
import ru.hse.orders.model.OrderResponse
import ru.hse.orders.model.OrderStatus
import ru.hse.core.events.PaymentRequestEvent
import java.math.BigDecimal
import java.util.*
import kotlin.time.Clock

class OrderService {
    fun createOrder(userId: String, amount: String, description: String?): String = transaction {
        val orderId = UUID.randomUUID()
        val now = Clock.System.now()
        
        // Insert order
        Orders.insert {
            it[id] = orderId
            it[Orders.userId] = userId
            it[Orders.amount] = BigDecimal(amount)
            it[Orders.description] = description
            it[Orders.status] = OrderStatus.NEW.name
            it[Orders.createdAt] = now
            it[Orders.updatedAt] = now
        }
        
        // Insert into outbox
        val paymentRequest = PaymentRequestEvent(
            orderId = orderId.toString(),
            userId = userId,
            amount = amount
        )
        
        OutboxTable.insert {
            it[OutboxTable.entityId] = orderId.toString()
            it[OutboxTable.eventType] = "payment-request"
            it[OutboxTable.payload] = Json.encodeToString(paymentRequest)
            it[OutboxTable.processed] = false
            it[OutboxTable.createdAt] = now
        }
        
        orderId.toString()
    }

    fun getOrders(userId: String): List<OrderResponse> = transaction {
        Orders.selectAll().where { Orders.userId eq userId }
            .map { row ->
                OrderResponse(
                    id = row[Orders.id].toString(),
                    userId = row[Orders.userId],
                    amount = row[Orders.amount].toString(),
                    description = row[Orders.description],
                    status = row[Orders.status],
                    createdAt = row[Orders.createdAt].toString(),
                    updatedAt = row[Orders.updatedAt].toString()
                )
            }
    }

    fun getOrder(orderId: String, userId: String): OrderResponse? = transaction {
        Orders.selectAll().where { (Orders.id eq UUID.fromString(orderId)) and (Orders.userId eq userId) }
            .firstOrNull()
            ?.let { row ->
                OrderResponse(
                    id = row[Orders.id].toString(),
                    userId = row[Orders.userId],
                    amount = row[Orders.amount].toString(),
                    description = row[Orders.description],
                    status = row[Orders.status],
                    createdAt = row[Orders.createdAt].toString(),
                    updatedAt = row[Orders.updatedAt].toString()
                )
            }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) = transaction {
        Orders.update({ Orders.id eq UUID.fromString(orderId) }) {
            it[Orders.status] = status.name
            it[Orders.updatedAt] = Clock.System.now()
        }
    }
}


