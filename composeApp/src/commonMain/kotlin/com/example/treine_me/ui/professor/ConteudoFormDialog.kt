package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ConteudoUpdateRequest
import com.example.treine_me.api.ConteudoResponse
import com.example.treine_me.ui.controls.*
import com.example.treine_me.services.FileUploadService
import com.example.treine_me.services.AulaService
import kotlinx.coroutines.launch

@Composable
fun ConteudoFormDialog(
    aulaTitle: String,
    produtoId: String,
    moduloId: String,
    aulaId: String,
    initialConteudo: ConteudoResponse? = null,
    onDismiss: () -> Unit,
    onSave: (ConteudoUpdateRequest) -> Unit
) {
    val fileUploadService = remember { FileUploadService() }
    val aulaService = remember { AulaService() }
    val scope = rememberCoroutineScope()
    
    // Estados do formulário
    var videoUrl by remember { mutableStateOf(initialConteudo?.urlVideo) }
    var richTextContent by remember { mutableStateOf(initialConteudo?.textoMarkdown ?: "") }
    var arquivoUrl by remember { mutableStateOf(initialConteudo?.arquivoUrl) }
    var selectedTemplate by remember { mutableStateOf<ContentTemplate?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var isLoadingContent by remember { mutableStateOf(false) }
    var hasLoadedContent by remember { mutableStateOf(initialConteudo != null) }
    
    // Carregar conteúdo atualizado da aula apenas se não foi passado conteúdo inicial
    LaunchedEffect(aulaId) {
        if (!hasLoadedContent) {
            isLoadingContent = true
            try {
                val response = aulaService.getAula(produtoId, moduloId, aulaId)
                println("DEBUG: Resposta do GET - success: ${response.success}")
                if (response.success) {
                    val aula = response.data
                    println("DEBUG: Aula carregada - titulo: ${aula?.titulo}")
                    if (aula != null) {
                        val conteudo = aula.conteudo
                        println("DEBUG: Conteúdo encontrado: ${conteudo != null}, urlVideo: ${conteudo?.urlVideo}")
                        if (conteudo != null) {
                            videoUrl = conteudo.urlVideo
                            richTextContent = conteudo.textoMarkdown ?: ""
                            arquivoUrl = conteudo.arquivoUrl
                        }
                    }
                    hasLoadedContent = true
                } else {
                    println("DEBUG: Erro na resposta: ${response.error?.message}")
                    error = response.error?.message ?: "Erro ao carregar conteúdo"
                }
            } catch (e: Exception) {
                println("DEBUG: Exception: ${e.message}")
                error = e.message ?: "Erro inesperado"
            } finally {
                isLoadingContent = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Column {
                Text("Conteúdo da Aula", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = aulaTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isLoadingContent) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Carregando conteúdo...")
                    }
                    Spacer(Modifier.height(16.dp))
                }

                if (error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Seletor de Templates
                ContentTemplateSelector(
                    selectedTemplate = selectedTemplate,
                    onTemplateSelected = { template ->
                        selectedTemplate = template
                        // Aplicar template ao rich text sempre que um template for selecionado
                        richTextContent = template.htmlContent
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(Modifier.height(16.dp))

                // Upload de Vídeo
                VideoUpload(
                    label = "Vídeo da Aula",
                    videoUrl = videoUrl,
                    onVideoUpload = { videoData ->
                        fileUploadService.uploadVideo(videoData)
                    },
                    onVideoDelete = { url ->
                        fileUploadService.deleteFile(url)
                    },
                    onVideoChanged = { newUrl ->
                        videoUrl = newUrl
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing
                )
                
                Spacer(Modifier.height(16.dp))

                // Rich Text Editor
                AppRichTextEditor(
                    value = richTextContent,
                    onValueChange = { richTextContent = it },
                    label = "Conteúdo da Aula",
                    placeholder = "Digite o conteúdo da aula aqui...",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing,
                    minLines = 8,
                    maxLines = 15
                )
                
                Spacer(Modifier.height(16.dp))

                // Upload de Arquivo Complementar
                OutlinedTextField(
                    value = arquivoUrl ?: "",
                    onValueChange = { /* Read-only por enquanto */ },
                    label = { Text("Arquivo Complementar") },
                    placeholder = { Text("PDF, documentos, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false // Por enquanto apenas placeholder
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Informações sobre os campos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Dicas:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "• Use templates para estruturar o conteúdo\n" +
                                    "• O editor de texto suporta formatação rica\n" +
                                    "• Vídeos são enviados automaticamente\n" +
                                    "• Todos os campos são opcionais",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            isProcessing = true
                            error = null
                            
                            val request = ConteudoUpdateRequest(
                                urlVideo = videoUrl,
                                textoMarkdown = richTextContent.takeIf { it.isNotBlank() },
                                arquivoUrl = arquivoUrl
                            )
                            onSave(request)
                        } catch (e: Exception) {
                            error = "Erro ao processar dados: ${e.message}"
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (initialConteudo != null) "Atualizar" else "Salvar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ConteudoCreateDialog(
    aulaTitle: String,
    produtoId: String,
    moduloId: String,
    aulaId: String,
    onDismiss: () -> Unit,
    onSave: (ConteudoUpdateRequest) -> Unit
) {
    ConteudoFormDialog(
        aulaTitle = aulaTitle,
        produtoId = produtoId,
        moduloId = moduloId,
        aulaId = aulaId,
        initialConteudo = null,
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@Composable
fun ConteudoEditDialog(
    aulaTitle: String,
    produtoId: String,
    moduloId: String,
    aulaId: String,
    initialConteudo: ConteudoResponse,
    onDismiss: () -> Unit,
    onSave: (ConteudoUpdateRequest) -> Unit
) {
    ConteudoFormDialog(
        aulaTitle = aulaTitle,
        produtoId = produtoId,
        moduloId = moduloId,
        aulaId = aulaId,
        initialConteudo = initialConteudo,
        onDismiss = onDismiss,
        onSave = onSave
    )
}
