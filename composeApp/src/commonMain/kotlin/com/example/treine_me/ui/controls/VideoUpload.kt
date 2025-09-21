package com.example.treine_me.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.treine_me.ui.util.ShowToast
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

/**
 * Componente para upload de vídeo único
 * 
 * @param label Texto do label do componente
 * @param videoUrl URL do vídeo atual (se existir)
 * @param onVideoUpload Callback para fazer upload do vídeo (retorna URL salva no servidor)
 * @param onVideoDelete Callback para deletar vídeo
 * @param onVideoChanged Callback quando o vídeo muda
 * @param modifier Modifier do componente
 * @param enabled Se o componente está habilitado para interação
 * @param showError Se deve mostrar estado de erro
 * @param errorMessage Mensagem de erro a ser exibida
 */
@Composable
fun VideoUpload(
    label: String,
    videoUrl: String? = null,
    onVideoUpload: suspend (VideoData) -> String, // retorna URL salva no servidor
    onVideoDelete: suspend (String) -> Unit,
    onVideoChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showError: Boolean = false,
    errorMessage: String? = null
) {
    var isUploading by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf(videoUrl) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var videoToDelete by remember { mutableStateOf<String?>(null) }
    var showVideoPlayer by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Atualizar URL local quando a prop videoUrl mudar
    LaunchedEffect(videoUrl) {
        currentVideoUrl = videoUrl
    }

    // Processar deleção de vídeo
    LaunchedEffect(videoToDelete) {
        videoToDelete?.let { video ->
            if (enabled && !isUploading) {
                isUploading = true
                uploadError = null
                try {
                    onVideoDelete(video)
                    currentVideoUrl = null
                    onVideoChanged(null)
                } catch (e: Exception) {
                    uploadError = "Erro ao deletar vídeo: ${e.message}"
                } finally {
                    isUploading = false
                    videoToDelete = null
                }
            }
        }
    }

    Column(modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(8.dp))

        if (currentVideoUrl != null) {
            // Mostrar vídeo existente
            VideoItem(
                videoUrl = currentVideoUrl!!,
                onDelete = {
                    if (enabled && !isUploading) {
                        videoToDelete = currentVideoUrl
                    }
                },
                onPlay = {
                    showVideoPlayer = true
                },
                enabled = enabled && !isUploading
            )
        } else {
            // Mostrar botão de upload
            AddVideoButton(
                onClick = {
                    if (!isUploading) {
                        scope.launch {
                            try {
                                val file = FileKit.openFilePicker()
                                if (file != null) {
                                    isUploading = true
                                    uploadError = null
                                    
                                    // Verificar tamanho do arquivo antes de carregar
                                    val bytes = file.readBytes()
                                    val fileSizeMB = bytes.size / (1024.0 * 1024.0)
                                    
                                    if (fileSizeMB > 300) {
                                        uploadError = "Arquivo muito grande (${fileSizeMB.toString().take(4)}MB). Máximo: 300MB"
                                        isUploading = false
                                        return@launch
                                    }
                                    
                                    println("📁 Arquivo selecionado (${fileSizeMB.toString().take(4)}MB)")
                                    
                                    val videoData = VideoData(
                                        bytes = bytes,
                                        fileName = "video.mp4", // Nome padrão já que não temos acesso ao nome real
                                        contentType = "video/mp4", // Tipo padrão
                                        size = bytes.size.toLong()
                                    )
                                    
                                    val savedUrl = onVideoUpload(videoData)
                                    currentVideoUrl = savedUrl
                                    onVideoChanged(savedUrl)
                                }
                            } catch (e: Exception) {
                                uploadError = "Erro ao fazer upload do vídeo: ${e.message}"
                            } finally {
                                isUploading = false
                            }
                        }
                    }
                },
                isUploading = isUploading,
                enabled = enabled
            )
        }

        // Mostrar erro se houver
        if (showError && errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            ErrorMessage(message = errorMessage)
        } else if (uploadError != null) {
            Spacer(Modifier.height(8.dp))
            ErrorMessage(message = uploadError!!)
            ShowToast(uploadError!!)
        }
    }

    // Dialog do player de vídeo
    if (showVideoPlayer && currentVideoUrl != null) {
        VideoPlayerDialog(
            videoUrl = currentVideoUrl!!,
            title = "Reproduzir Vídeo",
            onDismiss = { showVideoPlayer = false }
        )
    }
}

@Composable
private fun VideoItem(
    videoUrl: String,
    onDelete: () -> Unit,
    onPlay: () -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(enabled = enabled) { onPlay() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Thumbnail do vídeo (placeholder por enquanto)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.VideoFile,
                        contentDescription = "Vídeo",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Vídeo carregado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Botão de play sobreposto
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(24.dp)
                    )
                    .clickable(enabled = enabled) { onPlay() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Reproduzir vídeo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            if (enabled) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remover vídeo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddVideoButton(
    onClick: () -> Unit,
    isUploading: Boolean,
    enabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(enabled = enabled && !isUploading) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isUploading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Enviando vídeo...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Pode demorar alguns minutos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Adicionar vídeo",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Clique para adicionar vídeo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "MP4, MOV, AVI, MKV, WebM (até 300MB)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// VideoPlayerDialog agora está no VideoPlayer.kt

@Composable
private fun ErrorMessage(message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}
