package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import com.example.treine_me.api.ModuloResponse
import com.example.treine_me.api.AulaResponse
import com.example.treine_me.api.AulaCreateRequest
import com.example.treine_me.api.AulaUpdateRequest
import com.example.treine_me.api.ConteudoUpdateRequest
import com.example.treine_me.services.ModuloService
import com.example.treine_me.services.AulaService
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
 
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.material.icons.filled.DragHandle

@Composable
fun ModuloDetailScreen(
    produtoId: String,
    moduloId: String,
    onBack: () -> Unit
) {
    val moduloService = remember { ModuloService() }
    var modulo by remember { mutableStateOf<ModuloResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(produtoId) {
        isLoading = true
        errorMessage = null
        try {
            val list = moduloService.listModulos(produtoId)
            if (list.success) {
                modulo = list.data?.firstOrNull { it.id == moduloId }
            } else {
                errorMessage = list.error?.message ?: "Erro ao carregar módulo"
            }
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar") }
                Spacer(Modifier.width(8.dp))
                Text("Módulo", style = MaterialTheme.typography.titleLarge)
            }
        }
        Spacer(Modifier.height(16.dp))
        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            modulo?.let { m ->
                // Card resumo do módulo
                ModuloCard(modulo = m)
                Spacer(Modifier.height(16.dp))

                // Listagem/CRUD de aulas
                AulasSection(produtoId = produtoId, modulo = m)
            }
        }
    }
}

@Composable
private fun AulasSection(produtoId: String, modulo: ModuloResponse) {
    val aulaService = remember { AulaService() }
    val scope = rememberCoroutineScope()
    var aulas by remember { mutableStateOf<List<AulaResponse>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AulaResponse?>(null) }
    var deleting by remember { mutableStateOf<AulaResponse?>(null) }
    var editingContent by remember { mutableStateOf<AulaResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Carregar aulas sempre que entrar no módulo
    LaunchedEffect(produtoId, modulo.id) {
        isLoading = true
        error = null
        try {
            val response = aulaService.listAulas(produtoId, modulo.id)
            if (response.success) {
                aulas = response.data?.sortedBy { it.ordem } ?: emptyList()
            } else {
                error = response.error?.message ?: "Erro ao carregar aulas"
            }
        } catch (e: Exception) {
            error = e.message ?: "Erro inesperado"
        } finally {
            isLoading = false
        }
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Aulas", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = { showCreate = true }) { Icon(Icons.Default.Add, contentDescription = null) }
    }
    Spacer(Modifier.height(8.dp))
    if (error != null) {
        Text(error!!, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))
    }
    
    if (isLoading) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.width(8.dp))
            Text("Carregando aulas...")
        }
        return
    }

    val listState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(listState, onMove = { from, to ->
        val mutable = aulas.toMutableList()
        val moved = mutable.removeAt(from.index)
        mutable.add(to.index, moved)
        aulas = mutable
    })
    
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
    ) {
        items(aulas, key = { it.id }) { aula ->
            ReorderableItem(reorderState, key = aula.id) { _ ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(aula.titulo, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(2.dp))
                            Text(aula.descricao, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row {
                            IconButton(modifier = Modifier.draggableHandle(onDragStopped = {
                                // Persist order after drag completes
                                scope.launch {
                                    try {
                                        val resp = aulaService.reorderAulas(produtoId, modulo.id, aulas.map { it.id })
                                        if (!resp.success) {
                                            error = resp.error?.message ?: "Falha ao reordenar aulas"
                                        }
                                    } catch (e: Exception) {
                                        error = e.message
                                    }
                                }
                            }), onClick = {}) { Icon(Icons.Default.DragHandle, contentDescription = "Reordenar") }
                            IconButton(onClick = { editingContent = aula }) { Icon(Icons.Default.Info, contentDescription = "Gerenciar conteúdo") }
                            IconButton(onClick = { editing = aula }) { Icon(Icons.Default.Edit, contentDescription = null) }
                            IconButton(onClick = { deleting = aula }) { Icon(Icons.Default.Delete, contentDescription = null) }
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        AulaCreateDialog(
            onDismiss = { showCreate = false },
            onSave = { titulo, descricao, tipo, planoId ->
                scope.launch {
                    try {
                        val resp = aulaService.createAula(
                            produtoId = produtoId,
                            moduloId = modulo.id,
                            request = AulaCreateRequest(titulo, descricao, null, tipo, planoId) // ordem será calculada automaticamente
                        )
                        if (resp.success) {
                            val created = resp.data ?: run {
                                error = "Falha ao criar aula"
                                return@launch
                            }
                            aulas = (aulas + created).sortedBy { it.ordem }
                            showCreate = false
                        } else {
                            error = resp.error?.message ?: "Falha ao criar aula"
                        }
                    } catch (e: Exception) {
                        error = e.message
                    }
                }
            }
        )
    }

    if (editing != null) {
        val current = editing!!
        AulaEditDialog(
            initial = current,
            onDismiss = { editing = null },
            onSave = { titulo, descricao, tipo, planoId ->
                scope.launch {
                    try {
                        val resp = aulaService.updateAula(
                            produtoId = produtoId,
                            moduloId = modulo.id,
                            aulaId = current.id,
                            request = AulaUpdateRequest(titulo, descricao, null, tipo, planoId)
                        )
                        if (resp.success) {
                            val updated = resp.data ?: run {
                                error = "Falha ao atualizar aula"
                                return@launch
                            }
                            aulas = aulas.map { if (it.id == current.id) updated else it }.sortedBy { it.ordem }
                            editing = null
                        } else {
                            error = resp.error?.message ?: "Falha ao atualizar aula"
                        }
                    } catch (e: Exception) {
                        error = e.message
                    }
                }
            }
        )
    }

    if (deleting != null) {
        val target = deleting!!
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Excluir aula") },
            text = { Text("Deseja excluir a aula '${target.titulo}'?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            val resp = aulaService.deleteAula(produtoId = produtoId, moduloId = modulo.id, aulaId = target.id)
                            if (resp.success) {
                                aulas = aulas.filterNot { it.id == target.id }
                                deleting = null
                            } else {
                                error = resp.error?.message ?: "Falha ao excluir aula"
                            }
                        } catch (e: Exception) {
                            error = e.message
                        }
                    }
                }) { Text("Excluir") }
            },
            dismissButton = { Button(onClick = { deleting = null }) { Text("Cancelar") } }
        )
    }

    if (editingContent != null) {
        val current = editingContent!!
        ConteudoFormDialog(
            aulaTitle = current.titulo,
            produtoId = produtoId,
            moduloId = modulo.id,
            aulaId = current.id,
            initialConteudo = current.conteudo,
            onDismiss = { editingContent = null },
            onSave = { request ->
                scope.launch {
                    try {
                        val resp = aulaService.upsertConteudo(produtoId, modulo.id, current.id, request)
                        if (resp.success) {
                            // Refresh the aulas list to get updated content
                            val listResp = aulaService.listAulas(produtoId, modulo.id)
                            if (listResp.success) {
                                aulas = listResp.data?.sortedBy { it.ordem } ?: aulas
                            }
                            editingContent = null
                        } else {
                            error = resp.error?.message ?: "Falha ao salvar conteúdo"
                        }
                    } catch (e: Exception) {
                        error = e.message
                    }
                }
            }
        )
    }
}

 


