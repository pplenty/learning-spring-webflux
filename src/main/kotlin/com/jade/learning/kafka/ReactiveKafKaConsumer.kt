package com.jade.learning.kafka

import com.jade.learning.util.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.MDC
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Component
import reactor.util.function.Tuple2
import reactor.util.function.Tuples

@Component
class ReactiveKafKaConsumer(kafkaConsumerTemplate: ReactiveKafkaConsumerTemplate<String, Message>) {

    private val log = logger()

    init {
        kafkaConsumerTemplate
            .receiveAutoAck()
            .doOnNext { log.info("message key={}, topic={}, value={}", it.key(), it.topic(), it.value()) }
            .map { Tuples.of(it, parse(it.value())) }
            .onErrorContinue { error, record -> logConsumeError(error, record) }
            .doOnNext { handle(it.t2) }
            .onErrorContinue { error, tuple -> logHandleError(error, tuple) }
            .subscribe()
    }

    private fun parse(message: Message): Message {
        if (message.age == 78) {
            throw RuntimeException()
        }
        return message.toPerson()
    }

    private fun handle(message: Message) {
        if (message.age == 18) {
            throw RuntimeException()
        }
        println("value=${message}")
    }

    private fun logConsumeError(error: Throwable, record: Any) {
        if (record is ConsumerRecord<*, *>) {
            log.error(
                "메시지를 읽는 중에 에러가 발생했습니다. key={}, topic={}, partition={}, offset={}",
                record.key(),
                record.topic(),
                record.partition(),
                record.offset(),
                error,
            )
        } else {
            log.error("알 수 없는 kafka consume 에러가 발생했습니다.", error)
        }
    }

    private fun logHandleError(error: Throwable, tuple: Any) {
        if (tuple is Tuple2<*, *> && tuple.t1 is ConsumerRecord<*, *>) {
            val record = tuple.t1 as ConsumerRecord<*, *>
            val message = tuple.t2

            MDC.put("kafka_message_value", message.toString())
            log.error(
                "핸들링 중 에러가 발생했습니다. key={}, topic={}, partition={}, offset={}",
                record.key(),
                record.topic(),
                record.partition(),
                record.offset(),
                error
            )
            MDC.remove("kafka_message_value")
        }
    }
}
