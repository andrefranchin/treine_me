package com.example.treine_me.services

import com.example.treine_me.models.ApiResponse
import com.example.treine_me.models.FileUploadResponse
import com.example.treine_me.network.ApiClient
import com.example.treine_me.ui.controls.VideoData
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/**
 * Serviço para upload de arquivos (vídeos e documentos)
 */
class FileUploadService {
    private val client = ApiClient.uploadClient // Usar cliente com timeout maior para uploads
    
    /**
     * Faz upload de um vídeo e retorna a URL
     */
    suspend fun uploadVideo(videoData: VideoData): String {
        return try {
            println("📤 Iniciando upload do vídeo: ${videoData.fileName} (${videoData.bytes.size / 1024 / 1024}MB)")
            
            val response = client.post("/upload/video") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                videoData.bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, videoData.contentType)
                                    append(HttpHeaders.ContentDisposition, "filename=\"${videoData.fileName}\"")
                                }
                            )
                        }
                    )
                )
            }
            
            val apiResponse = response.body<com.example.treine_me.api.ApiResponse<com.example.treine_me.api.FileUploadResponse>>()
            if (apiResponse.success) {
                val data = apiResponse.data
                if (data != null) {
                    println("✅ Upload concluído com sucesso: ${data.url}")
                    return data.url
                }
            }
            throw Exception(apiResponse.error?.message ?: "Erro no upload do vídeo")
        } catch (e: Exception) {
            println("❌ Erro no upload: ${e.message}")
            throw Exception("Erro no upload do vídeo: ${e.message}")
        }
    }
    
    /**
     * Faz upload de um arquivo complementar e retorna a URL
     */
    suspend fun uploadFile(fileData: ByteArray, fileName: String, contentType: String): String {
        return try {
            val response = client.post("/upload/document") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                fileData,
                                Headers.build {
                                    append(HttpHeaders.ContentType, contentType)
                                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                                }
                            )
                        }
                    )
                )
            }
            
            val apiResponse = response.body<com.example.treine_me.api.ApiResponse<com.example.treine_me.api.FileUploadResponse>>()
            if (apiResponse.success) {
                val data = apiResponse.data
                if (data != null) {
                    return data.url
                }
            }
            throw Exception(apiResponse.error?.message ?: "Erro no upload do arquivo")
        } catch (e: Exception) {
            throw Exception("Erro no upload do arquivo: ${e.message}")
        }
    }
    
    /**
     * Remove um arquivo do servidor
     */
    suspend fun deleteFile(fileUrl: String) {
        // TODO: Implementar endpoint de deleção no backend se necessário
        // Por enquanto, apenas log
        println("Solicitação de deleção para: $fileUrl")
    }
    
    /**
     * Obtém informações sobre um arquivo (tamanho, tipo, etc.)
     */
    suspend fun getFileInfo(fileUrl: String): FileInfo? {
        // TODO: Implementar endpoint de informações do arquivo se necessário
        // Por enquanto, retorna informações básicas baseadas na URL
        return FileInfo(
            url = fileUrl,
            size = 0L, // Não disponível sem endpoint específico
            contentType = when {
                fileUrl.contains(".mp4") -> "video/mp4"
                fileUrl.contains(".pdf") -> "application/pdf"
                fileUrl.contains(".jpg") || fileUrl.contains(".jpeg") -> "image/jpeg"
                fileUrl.contains(".png") -> "image/png"
                fileUrl.contains(".webm") -> "video/webm"
                fileUrl.contains(".mov") -> "video/quicktime"
                else -> "application/octet-stream"
            },
            fileName = fileUrl.substringAfterLast("/")
        )
    }
}

/**
 * Informações sobre um arquivo
 */
data class FileInfo(
    val url: String,
    val size: Long,
    val contentType: String,
    val fileName: String
)