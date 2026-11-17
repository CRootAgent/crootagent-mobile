package com.crootagent.mobile.data.model

import com.google.gson.annotations.SerializedName

// Gemini API Models
data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

// OpenAI / Groq / Ollama compatible Models
data class OpenAiChatRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 1024,
    val temperature: Double = 0.7
)

data class OpenAiMessage(
    val role: String,
    val content: String
)

data class OpenAiChatResponse(
    val choices: List<OpenAiChoice>?
)

data class OpenAiChoice(
    val message: OpenAiMessage?
)
