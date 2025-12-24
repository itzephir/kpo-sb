package ru.hse.orders.outbox

import ru.hse.core.outbox.OutboxProcessor as CoreOutboxProcessor
import ru.hse.core.kafka.KafkaProducerService

class OutboxProcessor(
    kafkaProducer: KafkaProducerService,
) : CoreOutboxProcessor(kafkaProducer)

