package com.example.treine_me.network
import io.ktor.client.*
import io.ktor.client.call.body   // <- muito importante
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.ApiError
object ApiClient {

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }

        defaultRequest {
            // Prepend base URL so we can call with relative paths
            url(ApiConfig.baseUrl)
            // Default headers
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
            TokenStore.token?.let { token ->
                headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    suspend inline fun <reified T> get(path: String): T {
        val response: ApiResponse<T> = client.get(path).body()
        return handleApiResponse(response)
    }

    suspend inline fun <reified T> post(path: String, body: Any): T {
        val response: ApiResponse<T> = client.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
        return handleApiResponse(response)
    }

    private inline fun <reified T> handleApiResponse(api: ApiResponse<T>): T {
        if (api.success && api.data != null) return api.data
        val error: ApiError? = api.error
        throw ApiException(
            message = error?.message ?: "Erro desconhecido da API",
            details = error?.details,
            field = error?.field
        )
    }
}