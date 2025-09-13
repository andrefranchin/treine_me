package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CursoFormScreen(
    id: String?,
    onDone: () -> Unit
) {
    // TODO: Integrar com repositório real
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        if (errorMessage != null) {
            Text(errorMessage!!, color = androidx.compose.ui.graphics.Color.Red)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = foto, onValueChange = { foto = it }, label = { Text("Foto URL") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = {
                // TODO: Salvar curso
                onDone()
            }) { Text("Salvar") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onDone) { Text("Cancelar") }
        }
    }
}
