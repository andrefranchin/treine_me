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
    var showCreate by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(page) {
        val res = repo.list(page = page, size = 20)
        list = res.data
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Professores")
            Button(onClick = { showCreate = true }) { Text("Novo") }
        }
        Spacer(Modifier.height(8.dp))
        if (showCreate) {
            AppDialog(
                onDismissRequest = { showCreate = false },
                iconName = "person_add",
                title = { Text("Cadastrar Professor") },
                content = {
                    Column(Modifier.fillMaxWidth()) {
                        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = foto, onValueChange = { foto = it }, label = { Text("Foto URL") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            repo.create(
                                ProfessorCreateRequest(
                                    nome = nome,
                                    email = email,
                                    senha = senha,
                                    bio = bio.ifBlank { null },
                                    fotoPerfilUrl = foto.ifBlank { null }
                                )
                            )
                            val res = repo.list(page = page, size = 20)
                            list = res.data
                            nome = ""
                            email = ""
                            senha = ""
                            bio = ""
                            foto = ""
                            showCreate = false
                        }
                    }) { Text("Salvar") }
                },
                dismissButton = {
                    TextButton(onClick = { showCreate = false }) { Text("Cancelar") }
                }
            )
        }
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


