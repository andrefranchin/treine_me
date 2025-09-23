package com.example.treine_me.ui.aluno.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.ui.preview.VideoPreviewComposable

@Composable
fun VideoPreviewComponent(
    videoUrl: String?,
    productType: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(
                when (productType) {
                    "CURSO" -> Color(0xFF10B981).copy(alpha = 0.1f)
                    "MENTORIA" -> Color(0xFF8B5CF6).copy(alpha = 0.1f)
                    "EBOOK" -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                    else -> Color(0xFF6366F1).copy(alpha = 0.1f)
                },
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (videoUrl != null && videoUrl.isNotEmpty()) {
            // Exibe o preview do vídeo se houver URL
            VideoPreviewComposable(
                url = videoUrl,
                frameCount = 5,
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback para ícone quando não há vídeo
            Icon(
                when (productType) {
                    "CURSO" -> Icons.Default.PlayCircleOutline
                    "MENTORIA" -> Icons.Default.Person
                    "EBOOK" -> Icons.AutoMirrored.Filled.MenuBook
                    else -> Icons.Default.School
                },
                contentDescription = null,
                tint = when (productType) {
                    "CURSO" -> Color(0xFF10B981)
                    "MENTORIA" -> Color(0xFF8B5CF6)
                    "EBOOK" -> Color(0xFFF59E0B)
                    else -> Color(0xFF6366F1)
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
