package ru.hse.payments.service

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.hse.core.db.tables.OutboxTable
import ru.hse.core.events.PaymentStatusEvent
import ru.hse.payments.db.tables.Accounts
import ru.hse.payments.db.tables.PaymentAttempts
import java.math.BigDecimal
import java.util.UUID
import kotlin.time.Clock

class PaymentService {
    
    fun createAccount(userId: String): String = transaction {
        val accountId = UUID.randomUUID()
        val now = Clock.System.now()
        
        Accounts.insert {
            it[id] = accountId
            it[Accounts.userId] = userId
            it[Accounts.balance] = BigDecimal.ZERO
            it[Accounts.version] = 0L
            it[Accounts.createdAt] = now
            it[Accounts.updatedAt] = now
        }
        
        accountId.toString()
    }
    
    fun topupAccount(userId: String, amount: String): Boolean = transaction {
        val account = Accounts.selectAll().where { Accounts.userId eq userId }.firstOrNull()
            ?: return@transaction false
        
        val currentBalance = account[Accounts.balance]
        val currentVersion = account[Accounts.version]
        val newBalance = currentBalance + BigDecimal(amount)
        
        val updated = Accounts.update(
            where = { (Accounts.userId eq userId) and (Accounts.version eq currentVersion) }
        ) {
            it[Accounts.balance] = newBalance
            it[Accounts.version] = currentVersion + 1
            it[Accounts.updatedAt] = Clock.System.now()
        }
        
        updated > 0
    }
    
    fun getBalance(userId: String): BigDecimal? = transaction {
        Accounts.selectAll().where { Accounts.userId eq userId }
            .firstOrNull()
            ?.get(Accounts.balance)
    }
    
    fun processPayment(orderId: String, userId: String, amount: String): PaymentResult = transaction {
        // Check if payment already processed (idempotency)
        val existingAttempt = PaymentAttempts.selectAll().where { PaymentAttempts.orderId eq orderId }.firstOrNull()
        if (existingAttempt != null) {
            return@transaction PaymentResult(
                success = existingAttempt[PaymentAttempts.status] == "SUCCESS",
                message = "Payment already processed"
            )
        }
        
        // Check if account exists
        val account = Accounts.selectAll().where { Accounts.userId eq userId }.firstOrNull()
            ?: return@transaction PaymentResult(
                success = false,
                message = "Account not found"
            )
        
        val currentBalance = account[Accounts.balance]
        val currentVersion = account[Accounts.version]
        val paymentAmount = BigDecimal(amount)
        
        // Check if sufficient balance
        if (currentBalance < paymentAmount) {
            // Record failed attempt
            PaymentAttempts.insert {
                it[PaymentAttempts.orderId] = orderId
                it[PaymentAttempts.userId] = userId
                it[PaymentAttempts.amount] = paymentAmount
                it[PaymentAttempts.status] = "FAILED"
                it[PaymentAttempts.createdAt] = Clock.System.now()
            }
            
            // Create outbox event
            val statusEvent = PaymentStatusEvent(orderId = orderId, status = "FAILED")
            OutboxTable.insert {
                it[OutboxTable.entityId] = orderId
                it[OutboxTable.eventType] = "payment-status"
                it[OutboxTable.payload] = Json.encodeToString(statusEvent)
                it[OutboxTable.processed] = false
                it[OutboxTable.createdAt] = Clock.System.now()
            }
            
            return@transaction PaymentResult(
                success = false,
                message = "Insufficient balance"
            )
        }
        
        // Deduct amount with optimistic locking
        val newBalance = currentBalance - paymentAmount
        val updated = Accounts.update(
            where = { (Accounts.userId eq userId) and (Accounts.version eq currentVersion) }
        ) {
            it[Accounts.balance] = newBalance
            it[Accounts.version] = currentVersion + 1
            it[Accounts.updatedAt] = Clock.System.now()
        }
        
        if (updated == 0) {
            // Version conflict, retry needed
            return@transaction PaymentResult(
                success = false,
                message = "Concurrent modification, retry needed"
            )
        }
        
        // Record successful attempt
        PaymentAttempts.insert {
            it[PaymentAttempts.orderId] = orderId
            it[PaymentAttempts.userId] = userId
            it[PaymentAttempts.amount] = paymentAmount
            it[PaymentAttempts.status] = "SUCCESS"
            it[PaymentAttempts.createdAt] = Clock.System.now()
        }
        
        // Create outbox event
        val statusEvent = PaymentStatusEvent(orderId = orderId, status = "SUCCESS")
        OutboxTable.insert {
            it[OutboxTable.entityId] = orderId
            it[OutboxTable.eventType] = "payment-status"
            it[OutboxTable.payload] = Json.encodeToString(statusEvent)
            it[OutboxTable.processed] = false
            it[OutboxTable.createdAt] = Clock.System.now()
        }
        
        PaymentResult(success = true, message = "Payment processed successfully")
    }
}

data class PaymentResult(
    val success: Boolean,
    val message: String
)

