package com.jarvis.android.network

import retrofit2.http.*
import retrofit2.Response

data class ChatRequest(val message: String, val device_id: String = "android", val model: String = "jarvis-auto")
data class ChatResponse(val choices: List<Choice>, val tool_results: List<Any>? = null)
data class Choice(val message: Message)
data class Message(val role: String, val content: String)

data class DevicePairRequest(val device_id: String, val name: String, val platform: String = "android")
data class AutomationRequest(val name: String, val trigger_type: String, val trigger_config: Map<String, String>, val action: String)

interface JarvisApi {
    @POST("v1/ask")
    suspend fun ask(@Body req: ChatRequest): ChatResponse

    @POST("v1/chat/completions")
    suspend fun chatCompletions(@Body body: Map<String, Any>): Response<Map<String, Any>>

    @GET("v1/models")
    suspend fun listModels(): Map<String, Any>

    @POST("v1/device/pair")
    suspend fun pairDevice(@Body req: DevicePairRequest): Map<String, Any>

    @POST("v1/device/command")
    suspend fun sendCommand(@Body body: Map<String, Any>): Map<String, Any>

    @GET("v1/automation/list")
    suspend fun listAutomations(): Map<String, Any>

    @POST("v1/automation/create")
    suspend fun createAutomation(@Body req: AutomationRequest): Map<String, Any>

    @GET("v1/memory/recall")
    suspend fun recall(@Query("query") query: String): Map<String, Any>

    @POST("v1/memory/remember")
    suspend fun remember(@Body body: Map<String, String>): Map<String, Any>
}
