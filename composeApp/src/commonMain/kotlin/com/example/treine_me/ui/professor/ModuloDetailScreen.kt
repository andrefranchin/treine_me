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
import com.example.treine_me.api.TipoConteudo
import com.example.treine_me.services.ModuloService
import com.example.treine_me.services.AulaService
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.launch

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
    var aulas by remember { mutableStateOf<List<AulaResponse>>(modulo.aulas.sortedBy { it.ordem }) }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AulaResponse?>(null) }
    var deleting by remember { mutableStateOf<AulaResponse?>(null) }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Aulas", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = { showCreate = true }) { Icon(Icons.Default.Add, contentDescription = null) }
    }
    Spacer(Modifier.height(8.dp))
    if (error != null) {
        Text(error!!, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        aulas.forEach { aula ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(aula.titulo, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(2.dp))
                        Text(aula.descricao, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row {
                        IconButton(onClick = { editing = aula }) { Icon(Icons.Default.Edit, contentDescription = null) }
                        IconButton(onClick = { deleting = aula }) { Icon(Icons.Default.Delete, contentDescription = null) }
                    }
                }
            }
        }
    }

    if (showCreate) {
        AulaEditDialog(
            title = "Nova aula",
            initial = null,
            onDismiss = { showCreate = false },
            onSave = { titulo, descricao, ordem, tipo, planoId ->
                scope.launch {
                    try {
                        val resp = aulaService.createAula(
                            produtoId = produtoId,
                            moduloId = modulo.id,
                            request = AulaCreateRequest(titulo, descricao, ordem, tipo, planoId)
                        )
                        if (resp.success && resp.data != null) {
                            aulas = (aulas + resp.data!!).sortedBy { it.ordem }
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
            title = "Editar aula",
            initial = current,
            onDismiss = { editing = null },
            onSave = { titulo, descricao, ordem, tipo, planoId ->
                scope.launch {
                    try {
                        val resp = aulaService.updateAula(
                            produtoId = produtoId,
                            moduloId = modulo.id,
                            aulaId = current.id,
                            request = AulaUpdateRequest(titulo, descricao, ordem, tipo, planoId)
                        )
                        if (resp.success && resp.data != null) {
                            aulas = aulas.map { if (it.id == current.id) resp.data!! else it }.sortedBy { it.ordem }
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
}

@Composable
private fun AulaEditDialog(
    title: String,
    initial: AulaResponse?,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, TipoConteudo, String) -> Unit
) {
    var titulo by remember { mutableStateOf(initial?.titulo ?: "") }
    var descricao by remember { mutableStateOf(initial?.descricao ?: "") }
    var ordemText by remember { mutableStateOf((initial?.ordem ?: 1).toString()) }
    var planoId by remember { mutableStateOf(initial?.planoId ?: "") }
    var tipo by remember { mutableStateOf(initial?.tipoConteudo ?: TipoConteudo.VIDEO) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                if (error != null) { Text(error!!, color = MaterialTheme.colorScheme.error); Spacer(Modifier.height(8.dp)) }
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = ordemText, onValueChange = { ordemText = it.filter { ch -> ch.isDigit() } }, label = { Text("Ordem") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = planoId, onValueChange = { planoId = it }, label = { Text("Plano ID") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                // Simple enum switcher
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tipo:")
                    Spacer(Modifier.width(8.dp))
                    DropdownMenuWrapper(current = tipo, onChange = { tipo = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val ordem = ordemText.toIntOrNull()
                if (ordem == null || planoId.isBlank()) {
                    error = "Preencha ordem e plano"
                    return@Button
                }
                onSave(titulo, descricao, ordem, tipo, planoId)
            }) { Text("Salvar") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun DropdownMenuWrapper(current: TipoConteudo, onChange: (TipoConteudo) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) { Text(current.name) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TipoConteudo.values().forEach { tc ->
                DropdownMenuItem(text = { Text(tc.name) }, onClick = {
                    onChange(tc)
                    expanded = false
                })
            }
        }
    }
}


