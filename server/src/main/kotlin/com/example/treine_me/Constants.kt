package com.example.treine_me

const val SERVER_PORT = 8080

object Constants {
    // Admin padrão do sistema - em produção, usar variáveis de ambiente
    const val DEFAULT_ADMIN_EMAIL = "admin@treine-me.com"
    const val DEFAULT_ADMIN_PASSWORD = "admin123"
    const val DEFAULT_ADMIN_NAME = "Administrador"
    
    // Storage
    const val MAX_FILE_SIZE = 100 * 1024 * 1024 // 100MB
    val ALLOWED_IMAGE_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    val ALLOWED_VIDEO_TYPES = setOf("video/mp4", "video/webm", "video/quicktime")
    val ALLOWED_DOCUMENT_TYPES = setOf("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
}
