package ru.hse.payments.inbox

import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.hse.core.inbox.InboxProcessor as CoreInboxProcessor
import ru.hse.core.events.PaymentRequestEvent
import ru.hse.payments.service.PaymentService

class InboxProcessor : CoreInboxProcessor(), KoinComponent {

    private val paymentService: PaymentService by inject()

    override suspend fun processMessage(messageId: String, payload: String) {
        val event = Json.decodeFromString<PaymentRequestEvent>(payload)
        paymentService.processPayment(event.orderId, event.userId, event.amount)
    }
}

