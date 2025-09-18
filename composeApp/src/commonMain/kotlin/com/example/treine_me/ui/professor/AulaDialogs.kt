package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.AulaResponse
import com.example.treine_me.api.TipoConteudo
import com.example.treine_me.ui.controls.PlanoSelect

@Composable
fun AulaCreateDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, TipoConteudo, String) -> Unit
) {
    AulaFormDialog(
        title = "Nova aula",
        initial = null,
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@Composable
fun AulaEditDialog(
    initial: AulaResponse,
    onDismiss: () -> Unit,
    onSave: (String, String, TipoConteudo, String) -> Unit
) {
    AulaFormDialog(
        title = "Editar aula",
        initial = initial,
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@Composable
private fun AulaFormDialog(
    title: String,
    initial: AulaResponse?,
    onDismiss: () -> Unit,
    onSave: (String, String, TipoConteudo, String) -> Unit
) {
    var titulo by remember { mutableStateOf(initial?.titulo ?: "") }
    var descricao by remember { mutableStateOf(initial?.descricao ?: "") }
    var planoId by remember { mutableStateOf(initial?.planoId ?: "") }
    var tipo by remember { mutableStateOf(initial?.tipoConteudo ?: TipoConteudo.VIDEO) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                PlanoSelect(
                    selectedPlanoId = planoId.ifBlank { null },
                    onPlanoSelected = { planoId = it }
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tipo:")
                    Spacer(Modifier.width(8.dp))
                    DropdownMenuWrapper(current = tipo, onChange = { tipo = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (planoId.isBlank()) {
                    error = "Selecione um plano"
                    return@Button
                }
                onSave(titulo, descricao, tipo, planoId)
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
                DropdownMenuItem(
                    text = { Text(tc.name) },
                    onClick = {
                        onChange(tc)
                        expanded = false
                    }
                )
            }
        }
    }
}


