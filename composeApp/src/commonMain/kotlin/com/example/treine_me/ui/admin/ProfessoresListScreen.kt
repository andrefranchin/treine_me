package com.example.treine_me.ui.admin

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
import com.example.treine_me.api.ProfessorResponse
import com.example.treine_me.repository.AdminProfessoresRepository
import com.example.treine_me.ui.controls.AppDialog
import kotlinx.coroutines.launch

@Composable
fun ProfessoresListScreen(
    repo: AdminProfessoresRepository,
    onCreate: () -> Unit,
    onEdit: (String) -> Unit
) {
    var list by remember { mutableStateOf<List<ProfessorResponse>>(emptyList()) }
    var page by remember { mutableStateOf(1) }
    

    LaunchedEffect(page) {
        val res = repo.list(page = page, size = 20)
        list = res.data
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Professores")
            Button(onClick = { onCreate() }) { Text("Novo") }
        }
        Spacer(Modifier.height(8.dp))
        
        LazyColumn(Modifier.fillMaxSize()) {
            items(list, key = { it.id }) { prof ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(prof.nome)
                            Text(prof.email)
                        }
                        Row {
                            Button(onClick = { onEdit(prof.id) }) { Text("Editar") }
                        }
                    }
                }
            }
        }
    }
}


