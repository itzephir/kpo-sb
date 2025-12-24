package ru.hse.core.kafka

import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

class KafkaConsumerService(
    private val bootstrapServers: String,
    private val groupId: String,
    private val topic: String,
    private val messageHandler: suspend (key: String?, value: String) -> Unit
) : AutoCloseable {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val consumer: KafkaConsumer<String, String> by lazy {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
        }
        KafkaConsumer(props)
    }

    fun start() {
        scope.launch {
            consumer.subscribe(listOf(topic))
            
            while (isActive) {
                try {
                    val records = consumer.poll(Duration.ofMillis(1000))
                    
                    for (record in records) {
                        try {
                            messageHandler(record.key(), record.value())
                            consumer.commitSync()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Handle message processing failure
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(5000) // Wait before retrying
                }
            }
        }
    }

    override fun close() {
        scope.cancel()
        consumer.close()
    }
}