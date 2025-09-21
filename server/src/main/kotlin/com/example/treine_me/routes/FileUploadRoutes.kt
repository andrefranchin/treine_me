package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.services.FileUploadResponse
import com.example.treine_me.services.FileUploadService
import com.example.treine_me.storage.CloudflareR2Service
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.fileUploadRoutes() {
    val fileUploadService = FileUploadService(CloudflareR2Service())
    
    authenticate("auth-jwt") {
        route("/upload") {
            
            post("/profile-image") {
                val multipart = call.receiveMultipart(
                    formFieldLimit = 10L * 1024 * 1024 // 10MB para imagens
                )
                var fileName = ""
                var contentType = ""
                var fileBytes: ByteArray? = null
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName ?: "image"
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                
                if (fileBytes == null) {
                    return@post call.respond(ApiResponse.error("Nenhum arquivo foi enviado"))
                }
                
                val result = fileUploadService.uploadProfileImage(
                    fileName = fileName,
                    contentType = contentType,
                    inputStream = fileBytes!!.inputStream(),
                    fileSizeBytes = fileBytes!!.size.toLong()
                )
                
                if (result.success) {
                    val response = FileUploadResponse(
                        fileName = result.fileName,
                        url = result.url,
                        contentType = contentType,
                        size = fileBytes!!.size.toLong()
                    )
                    call.respond(ApiResponse.success(response))
                } else {
                    call.respond(ApiResponse.error(result.message ?: "Erro no upload"))
                }
            }
            
            // Rota "/upload/course-cover" removida aqui para evitar duplicidade.
            
            post("/video") {
                val multipart = call.receiveMultipart(
                    formFieldLimit = 350L * 1024 * 1024 // 350MB para vídeos
                )
                var fileName = ""
                var contentType = ""
                var fileBytes: ByteArray? = null
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName ?: "video"
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                
                if (fileBytes == null) {
                    return@post call.respond(ApiResponse.error("Nenhum arquivo foi enviado"))
                }
                
                val result = fileUploadService.uploadVideo(
                    fileName = fileName,
                    contentType = contentType,
                    inputStream = fileBytes!!.inputStream(),
                    fileSizeBytes = fileBytes!!.size.toLong()
                )
                
                if (result.success) {
                    val response = FileUploadResponse(
                        fileName = result.fileName,
                        url = result.url,
                        contentType = contentType,
                        size = fileBytes!!.size.toLong()
                    )
                    call.respond(ApiResponse.success(response))
                } else {
                    call.respond(ApiResponse.error(result.message ?: "Erro no upload"))
                }
            }
            
            post("/document") {
                val multipart = call.receiveMultipart(
                    formFieldLimit = 50L * 1024 * 1024 // 50MB para documentos
                )
                var fileName = ""
                var contentType = ""
                var fileBytes: ByteArray? = null
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName ?: "document"
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                
                if (fileBytes == null) {
                    return@post call.respond(ApiResponse.error("Nenhum arquivo foi enviado"))
                }
                
                val result = fileUploadService.uploadDocument(
                    fileName = fileName,
                    contentType = contentType,
                    inputStream = fileBytes!!.inputStream(),
                    fileSizeBytes = fileBytes!!.size.toLong()
                )
                
                if (result.success) {
                    val response = FileUploadResponse(
                        fileName = result.fileName,
                        url = result.url,
                        contentType = contentType,
                        size = fileBytes!!.size.toLong()
                    )
                    call.respond(ApiResponse.success(response))
                } else {
                    call.respond(ApiResponse.error(result.message ?: "Erro no upload"))
                }
            }
        }
    }
}
