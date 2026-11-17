package com.crootagent.mobile.data.api

import com.crootagent.mobile.data.model.GeminiRequest
import com.crootagent.mobile.data.model.GeminiResponse
import com.crootagent.mobile.data.model.OpenAiChatRequest
import com.crootagent.mobile.data.model.OpenAiChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}

interface OpenAiApiService {
    @POST
    suspend fun createChatCompletion(
        @Url url: String,
        @Header("Authorization") authorization: String?,
        @Body request: OpenAiChatRequest
    ): Response<OpenAiChatResponse>
}
