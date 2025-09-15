package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.ui.controls.ImagePicker
import com.example.treine_me.ui.controls.ImageData
import com.example.treine_me.services.ClientFileUploadService
import com.example.treine_me.ui.util.ShowToast
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import com.example.treine_me.services.ProdutoService
import com.example.treine_me.ui.controls.AppNetworkImage

@Composable
fun CursoTabsScreen(
    id: String?,
    onDone: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var cursoId by remember { mutableStateOf(id) }
    val tabTitles = listOf("Gerais", "Personalizacao")
    val canPersonalize = cursoId != null
    
    // Estado compartilhado entre as abas
    var cursoCover by remember { mutableStateOf<String?>(null) }

    var pendingToast by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    enabled = !(index == 1 && !canPersonalize),
                    onClick = {
                        if (index == 1 && !canPersonalize) {
                            pendingToast = "Salve o curso primeiro para habilitar Personalização"
                            return@Tab
                        }
                        selectedTabIndex = index
                    },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (pendingToast != null) {
            ShowToast(pendingToast!!)
            LaunchedEffect(pendingToast) { pendingToast = null }
        }

        when (selectedTabIndex) {
            0 -> CursoFormScreen(
                id = cursoId,
                onDone = onDone,
                cursoCover = cursoCover,
                onCreated = { newId ->
                    cursoId = newId
                    pendingToast = "Curso criado! Personalize na aba ao lado."
                    selectedTabIndex = 1
                }
            )
            1 -> CursoPersonalizacaoScreen(
                cursoId = cursoId,
                cursoCover = cursoCover,
                onCoverChanged = { cursoCover = it }
            )
        }
    }
}

@Composable
fun CursoPersonalizacaoScreen(
    cursoId: String?,
    cursoCover: String?,
    onCoverChanged: (String?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val uploadService = remember { ClientFileUploadService() }
    val produtoService = remember { ProdutoService() }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Em modo edição, buscar capa atual do curso (se ainda não carregada)
    LaunchedEffect(cursoId) {
        if (cursoId != null && cursoCover == null) {
            try {
                val resp = produtoService.getProduto(cursoId)
                if (resp.success) {
                    onCoverChanged(resp.data?.capaUrl)
                }
            } catch (_: Exception) {
                // Silenciar aqui; feedback não crítico
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Personalização do Curso",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Text(
            text = "Configure a aparência visual do seu curso",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(8.dp))

        // Selecionar e enviar imagem como capa do curso
        ImagePicker(
            enabled = !isUploading && cursoId != null,
            onImageSelected = { image: ImageData ->
                scope.launch {
                    isUploading = true
                    errorMessage = null
                    try {
                        val resp = uploadService.uploadCourseCover(
                            imageBytes = image.bytes,
                            fileName = image.fileName,
                            contentType = image.contentType,
                            produtoId = cursoId
                        )
                        onCoverChanged(resp.url)
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Falha no upload"
                    } finally {
                        isUploading = false
                    }
                }
            }
        )

        if (isUploading) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Enviando imagem...")
            }
        }

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            ShowToast(errorMessage!!)
        }
        
        if (cursoCover != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Capa Selecionada",
                        style = MaterialTheme.typography.titleMedium
                    )
                    AppNetworkImage(
                        url = cursoCover,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentDescription = "Capa do curso"
                    )
                    Text(
                        text = "URL: $cursoCover",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
