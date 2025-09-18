package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ProdutoResponse
import com.example.treine_me.services.ProdutoService

@Composable
fun CursosListScreen(
    onCreate: () -> Unit,
    onEdit: (String) -> Unit,
    onOpen: (String) -> Unit
) {
    val produtoService = remember { ProdutoService() }
    var cursos by remember { mutableStateOf<List<ProdutoResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            val resp = produtoService.listProdutos(page = 1, size = 50)
            if (resp.success) {
                cursos = resp.data?.data ?: emptyList()
            } else {
                error = resp.error?.message ?: "Falha ao carregar cursos"
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Cursos", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onCreate) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Novo")
            }
        }
        Spacer(Modifier.height(12.dp))

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        if (isLoading) {
            Text("Carregando...")
        } else if (cursos.isEmpty()) {
            Text("Nenhum curso encontrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cursos) { curso ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(curso.titulo, style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(4.dp))
                                    Text(curso.descricao, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row {
                                    IconButton(onClick = { onEdit(curso.id) }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { onOpen(curso.id) }) { Text("Abrir") }
                        }
                    }
                }
            }
        }
    }
}


