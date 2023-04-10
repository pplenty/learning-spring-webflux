package com.jade.learning.controller

import com.jade.learning.kafka.Message
import com.jade.learning.kafka.ReactiveKafKaProducer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/kafka")
@RestController
class KafkaController(
    val kafKaProducer: ReactiveKafKaProducer,
) {

    @GetMapping("/{topic}/send")
    fun produce(@PathVariable topic: String, name: String, age: Int) = kafKaProducer.send(topic, Message(name = name, age = age))
}
