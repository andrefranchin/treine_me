package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ModuloCreateRequest
import com.example.treine_me.services.ClientFileUploadService
import com.example.treine_me.services.ModuloService
import com.example.treine_me.ui.controls.ImagePicker
import com.example.treine_me.ui.controls.ImageData
import com.example.treine_me.ui.controls.AppNetworkImage
import com.example.treine_me.ui.util.ShowToast
import kotlinx.coroutines.launch

@Composable
fun ModuloTabsScreen(
    produtoId: String,
    onDone: () -> Unit,
    onCreated: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var moduloId by remember { mutableStateOf<String?>(null) }
    val tabTitles = listOf("Gerais", "Personalizacao")
    val canPersonalize = moduloId != null

    var capaUrl by remember { mutableStateOf<String?>(null) }
    var videoIntroUrl by remember { mutableStateOf<String?>(null) }
    var pendingToast by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    enabled = !(index == 1 && !canPersonalize),
                    onClick = {
                        if (index == 1 && !canPersonalize) {
                            pendingToast = "Salve o módulo primeiro para habilitar Personalização"
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
            0 -> ModuloFormTab(
                produtoId = produtoId,
                onSaved = { id, capa, video ->
                    moduloId = id
                    capaUrl = capa
                    videoIntroUrl = video
                    pendingToast = "Módulo criado! Personalize na aba ao lado."
                    selectedTabIndex = 1
                    onCreated()
                },
                onCancel = onDone
            )
            1 -> ModuloPersonalizacaoTab(
                moduloId = moduloId,
                capaUrl = capaUrl,
                onCapaChanged = { capaUrl = it },
                videoIntroUrl = videoIntroUrl,
                onVideoChanged = { videoIntroUrl = it }
            )
        }
    }
}

@Composable
private fun ModuloFormTab(
    produtoId: String,
    onSaved: (String, String?, String?) -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val moduloService = remember { ModuloService() }
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var ordemText by remember { mutableStateOf("1") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = ordemText, onValueChange = { ordemText = it.filter { ch -> ch.isDigit() } }, label = { Text("Ordem") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Row {
            Button(
                enabled = !isSaving,
                onClick = {
                    val ordem = ordemText.toIntOrNull() ?: 1
                    scope.launch {
                        isSaving = true
                        errorMessage = null
                        try {
                            val resp = moduloService.createModulo(
                                produtoId = produtoId,
                                request = ModuloCreateRequest(
                                    titulo = titulo,
                                    descricao = descricao,
                                    ordem = ordem
                                )
                            )
                            val created = resp.data
                            if (resp.success && created != null) {
                                onSaved(created.id, created.capaUrl, created.videoIntroUrl)
                            } else {
                                errorMessage = resp.error?.message ?: "Falha ao criar módulo"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Erro inesperado"
                        } finally {
                            isSaving = false
                        }
                    }
                }
            ) { if (isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Salvar") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onCancel) { Text("Cancelar") }
        }
    }
}

@Composable
private fun ModuloPersonalizacaoTab(
    moduloId: String?,
    capaUrl: String?,
    onCapaChanged: (String?) -> Unit,
    videoIntroUrl: String?,
    onVideoChanged: (String?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val uploadService = remember { ClientFileUploadService() }
    var isUploadingCapa by remember { mutableStateOf(false) }
    var isUploadingVideo by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Personalização do Módulo", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Envie capa e vídeo de introdução", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // Capa do módulo
        ImagePicker(
            enabled = !isUploadingCapa && moduloId != null,
            onImageSelected = { img: ImageData ->
                scope.launch {
                    isUploadingCapa = true
                    errorMessage = null
                    try {
                        val resp = uploadService.uploadModuleCover(
                            imageBytes = img.bytes,
                            fileName = img.fileName,
                            contentType = img.contentType,
                            moduloId = moduloId
                        )
                        onCapaChanged(resp.url)
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Falha no upload da capa"
                    } finally {
                        isUploadingCapa = false
                    }
                }
            }
        )

        if (capaUrl != null) {
            Card { Column(Modifier.padding(12.dp)) { Text("Capa selecionada", style = MaterialTheme.typography.titleSmall); Spacer(Modifier.height(8.dp)); AppNetworkImage(url = capaUrl, modifier = Modifier.fillMaxWidth().height(160.dp), contentDescription = "Capa do módulo") } }
        }

        // Vídeo de introdução do módulo
        // Reutiliza ImagePicker como seletor de arquivo binário; depende da implementação no projeto
        // Aqui assumimos que ImagePicker pode retornar bytes de vídeo também
        ImagePicker(
            enabled = !isUploadingVideo && moduloId != null,
            onImageSelected = { vid: ImageData ->
                scope.launch {
                    isUploadingVideo = true
                    errorMessage = null
                    try {
                        val resp = uploadService.uploadModuleIntroVideo(
                            videoBytes = vid.bytes,
                            fileName = vid.fileName,
                            contentType = vid.contentType,
                            moduloId = moduloId!!
                        )
                        onVideoChanged(resp.url)
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Falha no upload do vídeo"
                    } finally {
                        isUploadingVideo = false
                    }
                }
            }
        )

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            ShowToast(errorMessage!!)
        }

        if (videoIntroUrl != null) {
            Text(text = "Vídeo selecionado: $videoIntroUrl", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


