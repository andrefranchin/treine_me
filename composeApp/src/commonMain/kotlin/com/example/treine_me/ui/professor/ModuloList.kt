package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ModuloResponse
import com.example.treine_me.services.ModuloService
import com.example.treine_me.api.ModuloUpdateRequest
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.DialogProperties
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun ModuloList(
    produtoId: String,
    refreshSignal: Int,
    modifier: Modifier = Modifier,
    onOpenModulo: (ModuloResponse) -> Unit = {}
) {
    val service = remember { ModuloService() }
    val scope = rememberCoroutineScope()
    var modulos by remember { mutableStateOf<List<ModuloResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var moduloToDelete by remember { mutableStateOf<ModuloResponse?>(null) }
    var moduloToEdit by remember { mutableStateOf<ModuloResponse?>(null) }

    LaunchedEffect(produtoId, refreshSignal) {
        isLoading = true
        errorMessage = null
        try {
            val resp = service.listModulos(produtoId)
            val data = resp.data
            if (resp.success && data != null) {
                modulos = data.sortedBy { it.ordem }
            } else {
                errorMessage = resp.error?.message ?: "Falha ao carregar módulos"
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Erro inesperado ao carregar módulos"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> CircularProgressIndicator()
        errorMessage != null -> Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        modulos.isEmpty() -> Text(text = "Nenhum módulo cadastrado", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        else -> {
            val listState = rememberLazyListState()
            val reorderState = rememberReorderableLazyListState(listState, onMove = { from, to ->
                val mutable = modulos.toMutableList()
                val moved = mutable.removeAt(from.index)
                mutable.add(to.index, moved)
                modulos = mutable
            })
            
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(modulos, key = { it.id }) { modulo ->
                    ReorderableItem(reorderState, key = modulo.id) { _ ->
                        ModuloCard(
                            modulo = modulo,
                            onClick = onOpenModulo,
                            onEdit = { moduloToEdit = it },
                            onDelete = { moduloToDelete = it },
                            dragHandle = {
                                IconButton(
                                    modifier = Modifier.draggableHandle(onDragStopped = {
                                        // Persist new order after drag completes
                                        scope.launch {
                                            try {
                                                val resp = service.reorderModulos(produtoId, modulos.map { it.id })
                                                if (!resp.success) {
                                                    errorMessage = resp.error?.message ?: "Falha ao reordenar módulos"
                                                }
                                            } catch (e: Exception) {
                                                errorMessage = e.message ?: "Erro ao reordenar módulos"
                                            }
                                        }
                                    }),
                                    onClick = {}
                                ) {
                                    Icon(Icons.Default.DragHandle, contentDescription = "Reordenar")
                                }
                            }
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }

    if (moduloToDelete != null) {
        AlertDialog(
            onDismissRequest = { moduloToDelete = null },
            title = { Text("Excluir módulo") },
            text = { Text("Tem certeza que deseja excluir o módulo '${moduloToDelete!!.titulo}'? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(onClick = {
                    val target = moduloToDelete!!
                    moduloToDelete = null
                    scope.launch {
                        try {
                            val resp = service.deleteModulo(produtoId, target.id)
                            if (resp.success) {
                                modulos = modulos.filterNot { it.id == target.id }
                            } else {
                                errorMessage = resp.error?.message ?: "Falha ao excluir módulo"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Erro inesperado ao excluir"
                        }
                    }
                }) { Text("Excluir") }
            },
            dismissButton = { Button(onClick = { moduloToDelete = null }) { Text("Cancelar") } },
            properties = DialogProperties()
        )
    }

    if (moduloToEdit != null) {
        ModuloEditDialog(
            initial = moduloToEdit!!,
            onDismiss = { moduloToEdit = null },
            onSaved = { updated ->
                // persist
                scope.launch {
                    try {
                        val resp = service.updateModulo(
                            produtoId = produtoId,
                            moduloId = updated.id,
                            request = ModuloUpdateRequest(
                                titulo = updated.titulo,
                                descricao = updated.descricao,
                                ordem = updated.ordem,
                                capaUrl = updated.capaUrl,
                                videoIntroUrl = updated.videoIntroUrl
                            )
                        )
                        if (resp.success && resp.data != null) {
                            val newList = modulos.map { if (it.id == updated.id) resp.data!! else it }
                            modulos = newList.sortedBy { it.ordem }
                            moduloToEdit = null
                        } else {
                            errorMessage = resp.error?.message ?: "Falha ao salvar módulo"
                        }
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Erro inesperado ao salvar"
                    }
                }
            }
        )
    }
}


