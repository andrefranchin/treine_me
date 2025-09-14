package com.example.treine_me.services

import com.example.treine_me.network.ApiClient
import com.example.treine_me.network.ApiConfig
import com.example.treine_me.network.ApiException
import com.example.treine_me.api.ApiResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.Serializable

@Serializable
data class ClientUploadResponse(
    val fileName: String,
    val url: String,
    val contentType: String,
    val size: Long
)

class ClientFileUploadService {
    
    suspend fun uploadCourseCover(
        imageBytes: ByteArray,
        fileName: String,
        contentType: String,
        produtoId: String? = null
    ): ClientUploadResponse {
        val extra = if (produtoId != null) mapOf("produtoId" to produtoId) else emptyMap()
        return submitMultipart("/upload/course-cover", imageBytes, fileName, contentType, extra)
    }
    
    suspend fun uploadCourseGallery(
        imageBytes: ByteArray,
        fileName: String,
        contentType: String
    ): ClientUploadResponse {
        return submitMultipart("/upload/course-gallery", imageBytes, fileName, contentType)
    }
    
    suspend fun uploadModuleCover(
        imageBytes: ByteArray,
        fileName: String,
        contentType: String
    ): ClientUploadResponse {
        return submitMultipart("/upload/module-cover", imageBytes, fileName, contentType)
    }
    
    suspend fun uploadLessonCover(
        imageBytes: ByteArray,
        fileName: String,
        contentType: String
    ): ClientUploadResponse {
        return submitMultipart("/upload/lesson-cover", imageBytes, fileName, contentType)
    }
    
    suspend fun uploadGeneralPhotos(
        imageBytes: ByteArray,
        fileName: String,
        contentType: String
    ): ClientUploadResponse {
        return submitMultipart("/upload/general-photos", imageBytes, fileName, contentType)
    }

    private suspend fun submitMultipart(
        path: String,
        imageBytes: ByteArray,
        fileName: String,
        contentType: String,
        additionalFields: Map<String, String> = emptyMap()
    ): ClientUploadResponse {
        try {
            val httpResponse = ApiClient.client.submitFormWithBinaryData(
                url = "${ApiConfig.baseUrl}$path",
                formData = formData {
                    append("file", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, contentType)
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                    additionalFields.forEach { (k, v) ->
                        append(k, v)
                    }
                }
            )
            val envelope = httpResponse.body<ApiResponse<ClientUploadResponse>>()
            return ApiClient.handleApiResponse(envelope)
        } catch (e: ResponseException) {
            val message = extractApiErrorMessage(e.response) ?: e.message
            throw ApiException(message = message ?: "Falha no upload")
        } catch (e: Exception) {
            throw ApiException(message = e.message ?: "Falha no upload")
        }
    }

    private suspend fun extractApiErrorMessage(response: HttpResponse): String? {
        return try {
            val text = response.bodyAsText()
            val element = Json { ignoreUnknownKeys = true }.parseToJsonElement(text)
            val obj = element as? kotlinx.serialization.json.JsonObject
            val errorObj = obj?.get("error") as? kotlinx.serialization.json.JsonObject
            val messagePrimitive = errorObj?.get("message") ?: obj?.get("message")
            messagePrimitive?.jsonPrimitive?.contentOrNull ?: text
        } catch (_: Exception) {
            null
        }
    }
}


