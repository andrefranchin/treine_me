package com.example.treine_me.services

import com.example.treine_me.Constants
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.storage.StorageFolder
import com.example.treine_me.storage.StorageService
import com.example.treine_me.storage.UploadResult
import kotlinx.serialization.Serializable
import java.io.InputStream
import java.util.*

class FileUploadService(private val storageService: StorageService) {
    
    suspend fun uploadProfileImage(
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        fileSizeBytes: Long
    ): UploadResult {
        return uploadImage(fileName, contentType, inputStream, fileSizeBytes, StorageFolder.PROFILE_IMAGES.path)
    }
    
    suspend fun uploadImage(
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        fileSizeBytes: Long,
        folder: String
    ): UploadResult {
        validateImageUpload(contentType, fileSizeBytes)
        
        val sanitizedFileName = sanitizeFileName(fileName)
        val uniqueFileName = generateUniqueFileName(sanitizedFileName)
        
        return storageService.uploadFile(
            fileName = uniqueFileName,
            contentType = contentType,
            inputStream = inputStream,
            folder = folder
        )
    }
    
    suspend fun uploadCourseCover(
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        fileSizeBytes: Long
    ): UploadResult {
        validateImageUpload(contentType, fileSizeBytes)
        
        val sanitizedFileName = sanitizeFileName(fileName)
        val uniqueFileName = generateUniqueFileName(sanitizedFileName)
        
        return storageService.uploadFile(
            fileName = uniqueFileName,
            contentType = contentType,
            inputStream = inputStream,
            folder = StorageFolder.COURSE_COVERS.path
        )
    }
    
    suspend fun uploadVideo(
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        fileSizeBytes: Long
    ): UploadResult {
        validateVideoUpload(contentType, fileSizeBytes)
        
        val sanitizedFileName = sanitizeFileName(fileName)
        val uniqueFileName = generateUniqueFileName(sanitizedFileName)
        
        return storageService.uploadFile(
            fileName = uniqueFileName,
            contentType = contentType,
            inputStream = inputStream,
            folder = StorageFolder.COURSE_VIDEOS.path
        )
    }
    
    suspend fun uploadDocument(
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        fileSizeBytes: Long
    ): UploadResult {
        validateDocumentUpload(contentType, fileSizeBytes)
        
        val sanitizedFileName = sanitizeFileName(fileName)
        val uniqueFileName = generateUniqueFileName(sanitizedFileName)
        
        val folder = if (contentType == "application/pdf") {
            StorageFolder.EBOOKS.path
        } else {
            StorageFolder.COURSE_FILES.path
        }
        
        return storageService.uploadFile(
            fileName = uniqueFileName,
            contentType = contentType,
            inputStream = inputStream,
            folder = folder
        )
    }
    
    suspend fun deleteFile(fileUrl: String): Boolean {
        try {
            // Extrair o nome do arquivo e pasta da URL
            val urlParts = fileUrl.split("/")
            if (urlParts.size < 2) return false
            
            val fileName = urlParts.last()
            val folder = urlParts[urlParts.size - 2]
            
            return storageService.deleteFile(fileName, folder)
        } catch (e: Exception) {
            return false
        }
    }
    
    suspend fun generatePresignedUrl(fileUrl: String, expirationMinutes: Int = 60): String {
        try {
            val urlParts = fileUrl.split("/")
            if (urlParts.size < 2) return fileUrl
            
            val fileName = urlParts.last()
            val folder = urlParts[urlParts.size - 2]
            
            return storageService.generatePresignedUrl(fileName, folder, expirationMinutes)
        } catch (e: Exception) {
            return fileUrl
        }
    }
    
    private fun validateImageUpload(contentType: String, fileSizeBytes: Long) {
        if (contentType !in Constants.ALLOWED_IMAGE_TYPES) {
            throw ValidationException(
                message = "Tipo de arquivo não permitido. Tipos aceitos: ${Constants.ALLOWED_IMAGE_TYPES.joinToString(", ")}",
                field = "file"
            )
        }
        
        if (fileSizeBytes > Constants.MAX_FILE_SIZE) {
            throw ValidationException(
                message = "Arquivo muito grande. Tamanho máximo: ${Constants.MAX_FILE_SIZE / 1024 / 1024}MB",
                field = "file"
            )
        }
    }
    
    private fun validateVideoUpload(contentType: String, fileSizeBytes: Long) {
        if (contentType !in Constants.ALLOWED_VIDEO_TYPES) {
            throw ValidationException("Tipo de arquivo não permitido. Tipos aceitos: ${Constants.ALLOWED_VIDEO_TYPES.joinToString(", ")}")
        }
        
        if (fileSizeBytes > Constants.MAX_FILE_SIZE) {
            throw ValidationException("Arquivo muito grande. Tamanho máximo: ${Constants.MAX_FILE_SIZE / 1024 / 1024}MB")
        }
    }
    
    private fun validateDocumentUpload(contentType: String, fileSizeBytes: Long) {
        if (contentType !in Constants.ALLOWED_DOCUMENT_TYPES) {
            throw ValidationException("Tipo de arquivo não permitido. Tipos aceitos: ${Constants.ALLOWED_DOCUMENT_TYPES.joinToString(", ")}")
        }
        
        if (fileSizeBytes > Constants.MAX_FILE_SIZE) {
            throw ValidationException("Arquivo muito grande. Tamanho máximo: ${Constants.MAX_FILE_SIZE / 1024 / 1024}MB")
        }
    }
    
    private fun sanitizeFileName(fileName: String): String {
        // Remove caracteres especiais e espaços
        return fileName
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .replace(Regex("_{2,}"), "_")
            .lowercase()
    }
    
    private fun generateUniqueFileName(fileName: String): String {
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().take(8)
        val extension = fileName.substringAfterLast(".", "")
        val nameWithoutExtension = fileName.substringBeforeLast(".")
        
        return if (extension.isNotEmpty()) {
            "${nameWithoutExtension}_${timestamp}_${uuid}.${extension}"
        } else {
            "${fileName}_${timestamp}_${uuid}"
        }
    }
}

@Serializable
data class FileUploadResponse(
    val fileName: String,
    val url: String,
    val contentType: String,
    val size: Long
)
