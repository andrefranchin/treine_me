package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlunosListScreen() {
    // TODO: Integrar com repositório real
    var alunos by remember { mutableStateOf(listOf<AlunoUiModel>()) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Alunos")
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.fillMaxSize()) {
            items(alunos, key = { it.id }) { aluno ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(aluno.nome)
                            Text(aluno.email)
                        }
                    }
                }
            }
        }
    }
}

data class AlunoUiModel(
    val id: String,
    val nome: String,
    val email: String,
    val fotoUrl: String? = null
)
