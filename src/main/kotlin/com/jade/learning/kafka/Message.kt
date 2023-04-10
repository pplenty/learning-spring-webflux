package com.jade.learning.kafka

data class Message(
    val name: String,
    val age: Int,
    val first: String? = null,
) {
    fun toPerson(): Message = Message(name = this.name, age = this.age, first = this.name.first().toString())
}
