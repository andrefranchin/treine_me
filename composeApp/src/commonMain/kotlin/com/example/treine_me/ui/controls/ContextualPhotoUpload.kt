package com.example.treine_me.ui.controls

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Enum para diferentes contextos de upload de fotos
 */
enum class PhotoUploadContext {
    COURSE_COVER,      // Capa do curso
    COURSE_GALLERY,    // Galeria de fotos do curso
    MODULE_COVER,      // Capa do módulo
    LESSON_COVER,      // Capa da aula
    LESSON_GALLERY,    // Galeria de fotos da aula
    PROFILE_IMAGE,     // Foto de perfil
    GENERAL           // Uso geral
}

/**
 * Componente contextual para upload de fotos baseado no tipo de conteúdo
 */
@Composable
fun ContextualPhotoUpload(
    context: PhotoUploadContext,
    photos: List<String>,
    onAddPhoto: suspend (String) -> String,
    onDeletePhoto: suspend (String) -> Unit,
    onPhotosChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showError: Boolean = false,
    errorMessage: String? = null
) {
    val config = getPhotoUploadConfig(context)
    
    PhotoUploadGrid(
        label = config.label,
        photos = photos,
        maxPhotos = config.maxPhotos,
        onAddPhoto = onAddPhoto,
        onDeletePhoto = onDeletePhoto,
        onPhotosChanged = onPhotosChanged,
        modifier = modifier,
        enabled = enabled,
        showError = showError,
        errorMessage = errorMessage
    )
}

/**
 * Configuração específica para cada contexto de upload
 */
private data class PhotoUploadConfig(
    val label: String,
    val maxPhotos: Int,
    val description: String
)

private fun getPhotoUploadConfig(context: PhotoUploadContext): PhotoUploadConfig {
    return when (context) {
        PhotoUploadContext.COURSE_COVER -> PhotoUploadConfig(
            label = "Capa do Curso",
            maxPhotos = 1,
            description = "Imagem principal que representa o curso"
        )
        PhotoUploadContext.COURSE_GALLERY -> PhotoUploadConfig(
            label = "Galeria do Curso",
            maxPhotos = 10,
            description = "Fotos adicionais do curso"
        )
        PhotoUploadContext.MODULE_COVER -> PhotoUploadConfig(
            label = "Capa do Módulo",
            maxPhotos = 1,
            description = "Imagem representativa do módulo"
        )
        PhotoUploadContext.LESSON_COVER -> PhotoUploadConfig(
            label = "Capa da Aula",
            maxPhotos = 1,
            description = "Imagem da aula"
        )
        PhotoUploadContext.LESSON_GALLERY -> PhotoUploadConfig(
            label = "Galeria da Aula",
            maxPhotos = 5,
            description = "Fotos complementares da aula"
        )
        PhotoUploadContext.PROFILE_IMAGE -> PhotoUploadConfig(
            label = "Foto de Perfil",
            maxPhotos = 1,
            description = "Sua foto de perfil"
        )
        PhotoUploadContext.GENERAL -> PhotoUploadConfig(
            label = "Fotos",
            maxPhotos = 5,
            description = "Fotos gerais"
        )
    }
}

/**
 * Componente específico para capa de curso
 */
@Composable
fun CourseCoverUpload(
    coverUrl: String?,
    onCoverChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val photos = if (coverUrl != null) listOf(coverUrl) else emptyList()
    
    ContextualPhotoUpload(
        context = PhotoUploadContext.COURSE_COVER,
        photos = photos,
        onAddPhoto = { url -> 
            onCoverChanged(url)
            url
        },
        onDeletePhoto = { 
            onCoverChanged(null)
        },
        onPhotosChanged = { newPhotos ->
            onCoverChanged(newPhotos.firstOrNull())
        },
        modifier = modifier,
        enabled = enabled
    )
}

/**
 * Componente específico para galeria de curso
 */
@Composable
fun CourseGalleryUpload(
    photos: List<String>,
    onPhotosChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    ContextualPhotoUpload(
        context = PhotoUploadContext.COURSE_GALLERY,
        photos = photos,
        onAddPhoto = { url -> 
            val newPhotos = photos + url
            onPhotosChanged(newPhotos)
            url
        },
        onDeletePhoto = { url ->
            val newPhotos = photos - url
            onPhotosChanged(newPhotos)
        },
        onPhotosChanged = onPhotosChanged,
        modifier = modifier,
        enabled = enabled
    )
}

/**
 * Componente específico para capa de módulo
 */
@Composable
fun ModuleCoverUpload(
    coverUrl: String?,
    onCoverChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val photos = if (coverUrl != null) listOf(coverUrl) else emptyList()
    
    ContextualPhotoUpload(
        context = PhotoUploadContext.MODULE_COVER,
        photos = photos,
        onAddPhoto = { url -> 
            onCoverChanged(url)
            url
        },
        onDeletePhoto = { 
            onCoverChanged(null)
        },
        onPhotosChanged = { newPhotos ->
            onCoverChanged(newPhotos.firstOrNull())
        },
        modifier = modifier,
        enabled = enabled
    )
}

/**
 * Componente específico para galeria de aula
 */
@Composable
fun LessonGalleryUpload(
    photos: List<String>,
    onPhotosChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    ContextualPhotoUpload(
        context = PhotoUploadContext.LESSON_GALLERY,
        photos = photos,
        onAddPhoto = { url -> 
            val newPhotos = photos + url
            onPhotosChanged(newPhotos)
            url
        },
        onDeletePhoto = { url ->
            val newPhotos = photos - url
            onPhotosChanged(newPhotos)
        },
        onPhotosChanged = onPhotosChanged,
        modifier = modifier,
        enabled = enabled
    )
}
