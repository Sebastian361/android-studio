package com.example.ejemplografico2

data class Conversacion(
    val id: String,
    val status: String,
    val messages: List<Mensaje>
)

data class Mensaje(
    val content: String,
    val message_type: String = "outgoing"
)
