package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.exceptions.BusinessException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.ProdutoUpdateRequest
import com.example.treine_me.models.ModuloUpdateRequest
import com.example.treine_me.services.FileUploadResponse
import com.example.treine_me.services.FileUploadService
import com.example.treine_me.services.ProfessorService
import com.example.treine_me.storage.CloudflareR2Service
import com.example.treine_me.storage.StorageFolder
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.contextualUploadRoutes() {
    val fileUploadService = FileUploadService(CloudflareR2Service())
    
    authenticate("auth-jwt") {
        route("/upload") {
            
            // ========== CURSOS ==========
            
            // Upload de capa de curso
            post("/course-cover") {
                val multipart = call.receiveMultipart()
                var fileName = ""
                var contentType = ""
                var fileBytes: ByteArray? = null
                var produtoId: String? = null
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName ?: "image"
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                        }
                        is PartData.FormItem -> {
                            val name = part.name?.lowercase()
                            if (name == "produtoid" || name == "productid" || name == "courseid") {
                                produtoId = part.value
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                
                if (fileBytes == null) {
                    throw ValidationException(message = "Nenhum arquivo foi enviado", field = "file")
                }
                if (produtoId.isNullOrBlank()) {
                    produtoId = call.request.queryParameters["produtoId"]
                        ?: call.request.queryParameters["productId"]
                        ?: call.request.queryParameters["courseId"]
                }
                if (produtoId.isNullOrBlank()) {
                    throw ValidationException(message = "ID do produto/curso é obrigatório", field = "produtoId")
                }
                
                val result = fileUploadService.uploadImage(
                    fileName = fileName,
                    contentType = contentType,
                    inputStream = fileBytes!!.inputStream(),
                    fileSizeBytes = fileBytes!!.size.toLong(),
                    folder = StorageFolder.COURSE_COVERS.path
                )
                
                if (result.success) {
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    val professorService = ProfessorService()
                    professorService.updateProduto(
                        produtoId = produtoId!!,
                        request = ProdutoUpdateRequest(capaUrl = result.url),
                        professorId = professorId
                    )
                    val response = FileUploadResponse(
                        fileName = result.fileName,
                        url = result.url,
                        contentType = contentType,
                        size = fileBytes!!.size.toLong()
                    )
                    call.respond(ApiResponse.success(response))
                } else {
                    throw BusinessException(result.message ?: "Erro no upload de Capa do curso")
                }
            }
            
            // Upload de galeria de curso
            post("/course-gallery") {
                handleImageUpload(
                    call = call,
                    fileUploadService = fileUploadService,
                    folder = StorageFolder.COURSE_GALLERY.path,
                    description = "Galeria do curso"
                )
            }
            
            // ========== MÓDULOS ==========
            
            // Upload de capa de módulo e atualizar modulo.capaUrl
            post("/module-cover") {
                val multipart = call.receiveMultipart()
                var fileName = ""
                var contentType = ""
                var fileBytes: ByteArray? = null
                var moduloId: String? = null
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName ?: "image"
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                        }
                        is PartData.FormItem -> {
                            val name = part.name?.lowercase()
                            if (name == "moduloid" || name == "moduleid") {
                                moduloId = part.value
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                if (fileBytes == null) {
                    throw ValidationException(message = "Nenhum arquivo foi enviado", field = "file")
                }
                if (moduloId.isNullOrBlank()) {
                    moduloId = call.request.queryParameters["moduloId"] ?: call.request.queryParameters["moduleId"]
                }
                if (moduloId.isNullOrBlank()) {
                    throw ValidationException(message = "ID do módulo é obrigatório", field = "moduloId")
                }
                val result = fileUploadService.uploadImage(
                    fileName = fileName,
                    contentType = contentType,
                    inputStream = fileBytes!!.inputStream(),
                    fileSizeBytes = fileBytes!!.size.toLong(),
                    folder = StorageFolder.MODULE_COVERS.path
                )
                if (result.success) {
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    val professorService = ProfessorService()
                    professorService.updateModulo(
                        moduloId = moduloId!!,
                        request = ModuloUpdateRequest(capaUrl = result.url),
                        professorId = professorId
                    )
                    val response = FileUploadResponse(
                        fileName = result.fileName,
                        url = result.url,
                        contentType = contentType,
                        size = fileBytes!!.size.toLong()
                    )
                    call.respond(ApiResponse.success(response))
                } else {
                    throw BusinessException(result.message ?: "Erro no upload de Capa do módulo")
                }
            }
            
            // Upload de galeria de módulo
            post("/module-gallery") {
                handleImageUpload(
                    call = call,
                    fileUploadService = fileUploadService,
                    folder = StorageFolder.MODULE_GALLERY.path,
                    description = "Galeria do módulo"
                )
            }
            
            // ========== AULAS ==========
            
            // Upload de capa de aula
            post("/lesson-cover") {
                handleImageUpload(
                    call = call,
                    fileUploadService = fileUploadService,
                    folder = StorageFolder.LESSON_COVERS.path,
                    description = "Capa da aula"
                )
            }
            
            // Vídeo de introdução do módulo
            post("/module-intro-video") {
                val multipart = call.receiveMultipart(
                    formFieldLimit = 350L * 1024 * 1024 // 350MB para vídeos
                )
                var fileName = ""
                var contentType = ""
                var fileBytes: ByteArray? = null
                var moduloId: String? = null
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName ?: "video"
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                        }
                        is PartData.FormItem -> {
                            val name = part.name?.lowercase()
                            if (name == "moduloid" || name == "moduleid") {
                                moduloId = part.value
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                if (fileBytes == null) {
                    throw ValidationException(message = "Nenhum arquivo foi enviado", field = "file")
                }
                if (moduloId.isNullOrBlank()) {
                    moduloId = call.request.queryParameters["moduloId"] ?: call.request.queryParameters["moduleId"]
                }
                if (moduloId.isNullOrBlank()) {
                    throw ValidationException(message = "ID do módulo é obrigatório", field = "moduloId")
                }
                val result = fileUploadService.uploadVideo(
                    fileName = fileName,
                    contentType = contentType,
                    inputStream = fileBytes!!.inputStream(),
                    fileSizeBytes = fileBytes!!.size.toLong()
                )
                if (result.success) {
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    val professorService = ProfessorService()
                    professorService.updateModulo(
                        moduloId = moduloId!!,
                        request = ModuloUpdateRequest(videoIntroUrl = result.url),
                        professorId = professorId
                    )
                    val response = FileUploadResponse(
                        fileName = result.fileName,
                        url = result.url,
                        contentType = contentType,
                        size = fileBytes!!.size.toLong()
                    )
                    call.respond(ApiResponse.success(response))
                } else {
                    throw BusinessException(result.message ?: "Erro no upload do Vídeo de introdução do módulo")
                }
            }
            
            // Upload de galeria de aula
            post("/lesson-gallery") {
                handleImageUpload(
                    call = call,
                    fileUploadService = fileUploadService,
                    folder = StorageFolder.LESSON_GALLERY.path,
                    description = "Galeria da aula"
                )
            }
            
            // ========== USUÁRIOS ==========
            
            // Upload de foto de perfil (já existe, mas mantendo consistência)
            post("/profile-image") {
                handleImageUpload(
                    call = call,
                    fileUploadService = fileUploadService,
                    folder = StorageFolder.PROFILE_IMAGES.path,
                    description = "Foto de perfil"
                )
            }
            
            // ========== GERAL ==========
            
            // Upload de fotos gerais
            post("/general-photos") {
                handleImageUpload(
                    call = call,
                    fileUploadService = fileUploadService,
                    folder = StorageFolder.GENERAL_PHOTOS.path,
                    description = "Fotos gerais"
                )
            }
        }
    }
}

private suspend fun handleImageUpload(
    call: ApplicationCall,
    fileUploadService: FileUploadService,
    folder: String,
    description: String
) {
    val multipart = call.receiveMultipart()
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
        throw ValidationException(message = "Nenhum arquivo foi enviado", field = "file")
    }
    
    val result = fileUploadService.uploadImage(
        fileName = fileName,
        contentType = contentType,
        inputStream = fileBytes!!.inputStream(),
        fileSizeBytes = fileBytes!!.size.toLong(),
        folder = folder
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
        throw BusinessException(result.message ?: "Erro no upload de $description")
    }
}
