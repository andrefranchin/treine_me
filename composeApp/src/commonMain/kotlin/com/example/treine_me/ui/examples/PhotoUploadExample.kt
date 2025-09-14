package com.example.treine_me.ui.examples

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.ui.controls.*

/**
 * Exemplo de uso dos componentes de upload de fotos
 */
@Composable
fun PhotoUploadExample() {
    var courseCover by remember { mutableStateOf<String?>(null) }
    var courseGallery by remember { mutableStateOf<List<String>>(emptyList()) }
    var moduleCover by remember { mutableStateOf<String?>(null) }
    var lessonGallery by remember { mutableStateOf<List<String>>(emptyList()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Exemplos de Upload de Fotos",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Exemplo 1: Capa de Curso
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. Capa de Curso",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Upload de capa principal do curso (máximo 1 foto)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                
                CourseCoverUpload(
                    coverUrl = courseCover,
                    onCoverChanged = { courseCover = it },
                    enabled = true
                )
            }
        }
        
        // Exemplo 2: Galeria de Curso
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2. Galeria de Curso",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Upload de fotos adicionais do curso (máximo 10 fotos)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                
                CourseGalleryUpload(
                    photos = courseGallery,
                    onPhotosChanged = { courseGallery = it },
                    enabled = true
                )
            }
        }
        
        // Exemplo 3: Capa de Módulo
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "3. Capa de Módulo",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Upload de capa do módulo (máximo 1 foto)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                
                ModuleCoverUpload(
                    coverUrl = moduleCover,
                    onCoverChanged = { moduleCover = it },
                    enabled = true
                )
            }
        }
        
        // Exemplo 4: Galeria de Aula
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "4. Galeria de Aula",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Upload de fotos da aula (máximo 5 fotos)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                
                LessonGalleryUpload(
                    photos = lessonGallery,
                    onPhotosChanged = { lessonGallery = it },
                    enabled = true
                )
            }
        }
        
        // Exemplo 5: Upload Genérico
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "5. Upload Genérico",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Upload de fotos para uso geral (máximo 5 fotos)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                
                var generalPhotos by remember { mutableStateOf<List<String>>(emptyList()) }
                
                ContextualPhotoUpload(
                    context = PhotoUploadContext.GENERAL,
                    photos = generalPhotos,
                    onAddPhoto = { url -> 
                        val newPhotos = generalPhotos + url
                        generalPhotos = newPhotos
                        url
                    },
                    onDeletePhoto = { url ->
                        generalPhotos = generalPhotos - url
                    },
                    onPhotosChanged = { generalPhotos = it },
                    enabled = true
                )
            }
        }
        
        // Informações sobre os uploads
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Informações dos Uploads",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                
                Text("Capa do Curso: ${courseCover ?: "Nenhuma"}")
                Text("Galeria do Curso: ${courseGallery.size} fotos")
                Text("Capa do Módulo: ${moduleCover ?: "Nenhuma"}")
                Text("Galeria da Aula: ${lessonGallery.size} fotos")
            }
        }
    }
}

/**
 * Exemplo de integração com API real
 */
@Composable
fun PhotoUploadWithApiExample() {
    var courseCover by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Upload com API Real",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))
        
        CourseCoverUpload(
            coverUrl = courseCover,
            onCoverChanged = { courseCover = it },
            enabled = !isUploading
        )
        
        if (isUploading) {
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Fazendo upload...")
            }
        }
        
        if (errorMessage != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Simulação de chamadas para API
suspend fun uploadCourseCover(imageUrl: String): String {
    // Simulação de delay de rede
    kotlinx.coroutines.delay(2000)
    
    // Simulação de upload bem-sucedido
    return "https://picsum.photos/seed/cover/800/600"
}

suspend fun deleteCourseCover(imageUrl: String) {
    // Simulação de delay de rede
    kotlinx.coroutines.delay(1000)
    
    // Simulação de exclusão bem-sucedida
    println("Foto deletada: $imageUrl")
}
