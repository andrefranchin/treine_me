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
    
    // Cliente separado para uploads com timeout maior
    val uploadClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            level = LogLevel.INFO // Menos verbose para uploads
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 300_000 // 5 minutos para uploads
            connectTimeoutMillis = 30_000  // 30 segundos para conectar
            socketTimeoutMillis = 300_000  // 5 minutos para socket
        }

        defaultRequest {
            url(ApiConfig.baseUrl)
            TokenStore.token?.let { token ->
                headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    suspend inline fun <reified T> get(path: String): T {
        val response = client.get(path).body<ApiResponse<T>>()
        return handleApiResponse(response)
    }

    suspend inline fun <reified T> post(path: String, body: Any): T {
        val response = client.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<ApiResponse<T>>()
        return handleApiResponse(response)
    }

    suspend inline fun <reified T> put(path: String, body: Any): T {
        val response = client.put(path) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<ApiResponse<T>>()
        return handleApiResponse(response)
    }

    suspend inline fun <reified T> delete(path: String): T {
        val response = client.delete(path).body<ApiResponse<T>>()
        return handleApiResponse(response)
    }

    @PublishedApi
    internal fun <T> handleApiResponse(api: ApiResponse<T>): T {
        val data = api.data
        if (api.success && data != null) return data
        val error: ApiError? = api.error
        throw ApiException(
            message = error?.message ?: "Erro desconhecido da API",
            details = error?.details,
            field = error?.field
        )
    }
}