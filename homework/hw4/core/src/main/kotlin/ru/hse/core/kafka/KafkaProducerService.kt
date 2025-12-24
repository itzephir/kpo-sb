package ru.hse.core.kafka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class KafkaProducerService(
    private val bootstrapServers: String,
    private val topic: String
) : AutoCloseable {
    
    private val producer: KafkaProducer<String, String> by lazy {
        val props = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.ACKS_CONFIG, "all")
            put(ProducerConfig.RETRIES_CONFIG, 3)
            put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
        }
        KafkaProducer<String, String>(props)
    }

    suspend fun send(key: String, value: String): RecordMetadata? = withContext(Dispatchers.IO) {
        val record = ProducerRecord(topic, key, value)
        producer.send(record).get()
    }

    override fun close() {
        producer.close()
    }
}