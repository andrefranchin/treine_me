package com.example.treine_me.ui.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.treine_me.ui.util.ShowToast

/**
 * Componente genérico para upload de fotos em grid
 * 
 * @param label Texto do label do componente
 * @param photos Lista de URLs das fotos atuais
 * @param maxPhotos Número máximo de fotos permitidas
 * @param onAddPhoto Callback para adicionar nova foto (retorna URL da foto salva)
 * @param onDeletePhoto Callback para deletar foto
 * @param onPhotosChanged Callback quando a lista de fotos muda
 * @param modifier Modifier do componente
 * @param enabled Se o componente está habilitado para interação
 * @param showError Se deve mostrar estado de erro
 * @param errorMessage Mensagem de erro a ser exibida
 * @param onAddPhotoClick Callback opcional acionado ao clicar em adicionar (sem mock interno)
 */
@Composable
fun PhotoUploadGrid(
    label: String,
    photos: List<String>,
    maxPhotos: Int = 5,
    onAddPhoto: suspend (String) -> String, // retorna URL salva no servidor
    onDeletePhoto: suspend (String) -> Unit,
    onPhotosChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showError: Boolean = false,
    errorMessage: String? = null,
    onAddPhotoClick: (() -> Unit)? = null
) {
    var isUploading by remember { mutableStateOf(false) }
    var currentPhotos by remember { mutableStateOf(photos) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var photoToDelete by remember { mutableStateOf<String?>(null) }

    // Atualizar lista local quando a prop photos mudar
    LaunchedEffect(photos) {
        currentPhotos = photos
    }

    // Processar deleção de foto
    LaunchedEffect(photoToDelete) {
        photoToDelete?.let { photo ->
            if (enabled && !isUploading) {
                isUploading = true
                uploadError = null
                try {
                    onDeletePhoto(photo)
                    currentPhotos = currentPhotos - photo
                    onPhotosChanged(currentPhotos)
                } catch (e: Exception) {
                    uploadError = "Erro ao deletar foto: ${e.message}"
                } finally {
                    isUploading = false
                    photoToDelete = null
                }
            }
        }
    }

    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
            
            if (currentPhotos.isNotEmpty()) {
                Text(
                    text = "${currentPhotos.size}/$maxPhotos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.heightIn(max = 300.dp),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(currentPhotos) { photo ->
                PhotoItem(
                    photoUrl = photo,
                    onDelete = {
                        if (enabled && !isUploading) {
                            photoToDelete = photo
                        }
                    },
                    enabled = enabled && !isUploading
                )
            }

            if (currentPhotos.size < maxPhotos && enabled) {
                item {
                    AddPhotoButton(
                        onClick = {
                            if (!isUploading) {
                                // Sem mock interno. Delega a ação para o chamador, se fornecido
                                onAddPhotoClick?.invoke()
                            }
                        },
                        isUploading = isUploading
                    )
                }
            }
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
}

@Composable
private fun PhotoItem(
    photoUrl: String,
    onDelete: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        // TODO: Implementar carregamento de imagem com Coil ou outra biblioteca
        // Por enquanto, mostrar placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = "Imagem",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (enabled) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(4.dp)
                    )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remover foto",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddPhotoButton(
    onClick: () -> Unit,
    isUploading: Boolean
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = !isUploading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isUploading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Adicionar foto",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Adicionar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

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
