package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ModuloResponse

@Composable
fun ModuloEditDialog(
    initial: ModuloResponse,
    onDismiss: () -> Unit,
    onSaved: (ModuloResponse) -> Unit
) {
    var titulo by remember { mutableStateOf(initial.titulo) }
    var descricao by remember { mutableStateOf(initial.descricao) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar módulo") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onSaved(initial.copy(titulo = titulo, descricao = descricao))
            }) { Text("Salvar") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancelar") } }
    )
}


