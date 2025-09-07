package com.example.treine_me.storage

import kotlinx.serialization.Serializable
import java.io.InputStream

interface StorageService {
    suspend fun uploadFile(
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        folder: String = ""
    ): UploadResult
    
    suspend fun deleteFile(fileName: String, folder: String = ""): Boolean
    
    suspend fun getFileUrl(fileName: String, folder: String = ""): String
    
    suspend fun generatePresignedUrl(
        fileName: String, 
        folder: String = "", 
        expirationMinutes: Int = 60
    ): String
}

@Serializable
data class UploadResult(
    val success: Boolean,
    val fileName: String,
    val url: String,
    val message: String? = null
)

enum class StorageFolder(val path: String) {
    PROFILE_IMAGES("profile-images"),
    COURSE_COVERS("course-covers"),
    COURSE_VIDEOS("course-videos"),
    COURSE_FILES("course-files"),
    EBOOKS("ebooks")
}
