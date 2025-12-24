package ru.hse.core.outbox

import kotlinx.coroutines.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.hse.core.db.tables.OutboxTable
import ru.hse.core.kafka.KafkaProducerService
import kotlin.time.Clock

open class OutboxProcessor(
    private val kafkaProducer: KafkaProducerService,
) : AutoCloseable {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun start() {
        scope.launch {
            while (isActive) {
                try {
                    processOutbox()
                    delay(1000) // Process every second
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(5000) // Wait longer on error
                }
            }
        }
    }

    private suspend fun processOutbox() = withContext(Dispatchers.IO) {
        val unprocessed = transaction {
            OutboxTable.selectAll()
                .where { OutboxTable.processed eq false }
                .limit(10)
                .map { row ->
                    Triple(
                        row[OutboxTable.id].value,
                        row[OutboxTable.entityId],
                        row[OutboxTable.payload]
                    )
                }
        }

        unprocessed.forEach { (outboxId, entityId, payload) ->
            try {
                kafkaProducer.send(entityId, payload)

                transaction {
                    OutboxTable.update({ OutboxTable.id eq outboxId }) {
                        it[OutboxTable.processed] = true
                        it[OutboxTable.processedAt] = Clock.System.now()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Will retry on next iteration
            }
        }
    }

    override fun close() {
        scope.cancel()
    }
}