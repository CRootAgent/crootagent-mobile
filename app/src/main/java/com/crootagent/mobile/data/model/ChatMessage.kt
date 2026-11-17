package com.crootagent.mobile.data.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "assistant"
    val content: String,
    val model: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
