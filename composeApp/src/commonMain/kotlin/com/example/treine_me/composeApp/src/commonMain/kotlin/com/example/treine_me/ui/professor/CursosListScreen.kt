package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.PaginatedResponse
import com.example.treine_me.api.ProdutoResponse
import com.example.treine_me.api.TipoProduto
import kotlinx.coroutines.launch
import com.example.treine_me.services.ProdutoService
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import com.example.treine_me.ui.controls.AppNetworkImage
@Composable
fun CursosListScreen(
    onCreate: () -> Unit,
    onEdit: (String) -> Unit,
    onOpen: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val produtoService = remember { ProdutoService() }
    var cursos by remember { mutableStateOf(listOf<CursoUiModel>()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        try {
            val resp = produtoService.listProdutos(page = 1, size = 50)
            if (resp.success) {
                val page: PaginatedResponse<ProdutoResponse>? = resp.data
                val onlyCursos = page?.data?.filter { it.tipo == TipoProduto.CURSO } ?: emptyList()
                cursos = onlyCursos.map {
                    CursoUiModel(
                        id = it.id,
                        titulo = it.titulo,
                        descricao = it.descricao,
                        capaUrl = it.capaUrl
                    )
                }
            } else {
                errorMessage = resp.error?.message ?: "Erro ao listar cursos"
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Erro inesperado"
        } finally {
            isLoading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Cursos")
            Button(onClick = onCreate) { Text("Novo") }
        }
        Spacer(Modifier.height(8.dp))
        if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
        }
        // TODO: Dialog de criação rápida
        LazyColumn(Modifier.fillMaxSize()) {
            items(cursos, key = { it.id }) { curso ->
                CursoListItem(
                    curso = curso,
                    onOpen = onOpen,
                    onEdit = onEdit,
                    onDelete = { id ->
                        scope.launch {
                            try {
                                val resp = produtoService.deleteProduto(id)
                                if (resp.success == true) {
                                    cursos = cursos.filterNot { it.id == id }
                                } else {
                                    errorMessage = resp.error?.message ?: "Falha ao excluir"
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Erro ao excluir"
                            }
                        }
                    }
                )
            }
        }
    }
}

data class CursoUiModel(
    val id: String,
    val titulo: String,
    val descricao: String,
    val capaUrl: String? = null
)

@Composable
private fun CursoListItem(
    curso: CursoUiModel,
    onOpen: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    println("Capa do curso: ${curso.capaUrl}")
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onOpen(curso.id) }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (curso.capaUrl != null) {
                    AppNetworkImage(
                        url = curso.capaUrl,
                        modifier = Modifier.size(56.dp),
                        contentDescription = "Capa do curso"
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(curso.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(curso.descricao, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row {
                IconButton(onClick = {
                    // Avoid parent click
                    onEdit(curso.id)
                }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = {
                    onDelete(curso.id)
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Excluir")
                }
            }
        }
    }
}
