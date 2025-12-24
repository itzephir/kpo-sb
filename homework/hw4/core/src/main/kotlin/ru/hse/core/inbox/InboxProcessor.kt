package ru.hse.core.inbox

import kotlinx.coroutines.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.hse.core.db.tables.InboxTable
import kotlin.time.Clock

abstract class InboxProcessor : AutoCloseable {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun start() {
        scope.launch {
            while (isActive) {
                try {
                    processInbox()
                    delay(1000) // Process every second
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(5000) // Wait longer on error
                }
            }
        }
    }

    private suspend fun processInbox() = withContext(Dispatchers.IO) {
        val unprocessed = transaction {
            InboxTable.selectAll().where { InboxTable.processed eq false }
                .limit(10)
                .map { row ->
                    Triple(
                        row[InboxTable.id].value,
                        row[InboxTable.messageId],
                        row[InboxTable.payload]
                    )
                }
        }

        unprocessed.forEach { (inboxId, messageId, payload) ->
            try {
                processMessage(messageId, payload)

                transaction {
                    InboxTable.update(where = { InboxTable.id eq inboxId }) {
                        it[InboxTable.processed] = true
                        it[InboxTable.processedAt] = Clock.System.now()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Will retry on next iteration
            }
        }
    }

    abstract suspend fun processMessage(messageId: String, payload: String)

    override fun close() {
        scope.cancel()
    }
}