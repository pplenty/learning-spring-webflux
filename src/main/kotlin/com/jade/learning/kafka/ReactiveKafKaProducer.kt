package com.jade.learning.kafka

import com.jade.learning.util.logger
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component

@Component
class ReactiveKafKaProducer(val kafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>) {

    private val log = logger()

    fun send(topic: String, message: String) {
        kafkaProducerTemplate.send(ProducerRecord(topic, message))
            .doOnError { error -> log.error("카프카 전송 실패 ({}): {}", topic, message, error) }.subscribe()
    }
}
