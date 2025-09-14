package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ProfessorCreateRequest
import com.example.treine_me.ui.controls.AppDialog
import kotlinx.coroutines.launch
import com.example.treine_me.ui.professor.CursoFormScreen
@Composable
fun CursosListScreen(
    onCreate: () -> Unit,
    onEdit: (String) -> Unit
) {
    // TODO: Integrar com repositório real
    var cursos by remember { mutableStateOf(listOf<CursoUiModel>()) }
    var showCreate by remember { mutableStateOf(false) }
    if (showCreate) {
        AppDialog(
            onDismissRequest = { showCreate = false },
            iconName = "person_add",
            title = { Text("Cadastrar Curso") },
            content = {
                CursoTabsScreen(null, onDone = { showCreate = false })
            },
            confirmButton = {
                TextButton(onClick = {
                    showCreate = false
                }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Cancelar") }
            }
        )
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Cursos")
            Button(onClick = { showCreate = true }) { Text("Novo") }
        }
        Spacer(Modifier.height(8.dp))
        // TODO: Dialog de criação rápida
        LazyColumn(Modifier.fillMaxSize()) {
            items(cursos, key = { it.id }) { curso ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(curso.titulo)
                            Text(curso.descricao)
                        }
                        Row {
                            Button(onClick = { onEdit(curso.id) }) { Text("Editar") }
                        }
                    }
                }
            }
        }
    }
}

data class CursoUiModel(
    val id: String,
    val titulo: String,
    val descricao: String,
    val fotoUrl: String? = null
)
