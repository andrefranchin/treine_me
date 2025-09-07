package com.example.treine_me.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ProfessorCreateRequest
import com.example.treine_me.api.ProfessorUpdateRequest
import com.example.treine_me.repository.AdminProfessoresRepository
import kotlinx.coroutines.launch

@Composable
fun ProfessorFormScreen(
    repo: AdminProfessoresRepository,
    id: String?,
    onDone: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(id) {
        if (id != null) {
            val prof = repo.get(id)
            nome = prof.nome
            email = prof.email
            bio = prof.bio.orEmpty()
            foto = prof.fotoPerfilUrl.orEmpty()
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(if (id == null) "Cadastrar Professor" else "Editar Professor")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        if (id == null) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = foto, onValueChange = { foto = it }, label = { Text("Foto URL") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = {
                scope.launch {
                    if (id == null) {
                        repo.create(ProfessorCreateRequest(nome = nome, email = email, senha = senha, bio = bio.ifBlank { null }, fotoPerfilUrl = foto.ifBlank { null }))
                    } else {
                        repo.update(id, ProfessorUpdateRequest(nome = nome, bio = bio.ifBlank { null }, fotoPerfilUrl = foto.ifBlank { null }))
                    }
                    onDone()
                }
            }) { Text("Salvar") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onDone) { Text("Cancelar") }
        }
    }
}


