package com.jade.learning.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.kafka.receiver.ReceiverOptions
import java.util.Collections

@Configuration
class ReactiveKafkaConsumerConfig {
    @Bean
    fun kafkaConsumerTemplate(
        @Value(value = "\${kafka.topic.test}") topic: String,
        kafkaProperties: KafkaProperties,
    ): ReactiveKafkaConsumerTemplate<String, String> {
        return ReactiveKafkaConsumerTemplate(
            ReceiverOptions.create<String, String>(kafkaProperties.buildConsumerProperties())
                .subscription(Collections.singleton(topic))
        )
    }
}