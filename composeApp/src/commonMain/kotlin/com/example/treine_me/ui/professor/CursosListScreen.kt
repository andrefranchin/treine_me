package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ProdutoResponse
import com.example.treine_me.services.ProdutoService
import com.example.treine_me.services.CourseStatsService
import com.example.treine_me.services.CourseStats
import com.example.treine_me.ui.components.CourseCard
import kotlinx.coroutines.launch

@Composable
fun CursosListScreen(
    onCreate: () -> Unit,
    onEdit: (String) -> Unit,
    onOpen: (String) -> Unit
) {
    val produtoService = remember { ProdutoService() }
    val statsService = remember { CourseStatsService() }
    val scope = rememberCoroutineScope()
    var cursos by remember { mutableStateOf<List<ProdutoResponse>>(emptyList()) }
    var courseStats by remember { mutableStateOf<Map<String, CourseStats>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            val resp = produtoService.listProdutos(page = 1, size = 50)
            if (resp.success && resp.data != null) {
                val cursosList = resp.data!!.data
                cursos = cursosList
                
                // Load stats for each course
                val statsMap = mutableMapOf<String, CourseStats>()
                cursosList.forEach { curso ->
                    try {
                        val statsResp = statsService.getCourseStats(curso.id)
                        if (statsResp.success && statsResp.data != null) {
                            statsMap[curso.id] = statsResp.data!!
                        }
                    } catch (e: Exception) {
                        // Ignore individual stats errors
                    }
                }
                courseStats = statsMap
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
                    CourseCard(
                        curso = curso,
                        stats = courseStats[curso.id],
                        onOpen = onOpen,
                        onEdit = onEdit,
                        onDelete = { cursoId ->
                            scope.launch {
                                try {
                                    val deleteResp = produtoService.deleteProduto(cursoId)
                                    if (deleteResp.success) {
                                        // Remove from local list
                                        cursos = cursos.filterNot { it.id == cursoId }
                                        courseStats = courseStats.filterNot { it.key == cursoId }
                                    } else {
                                        error = deleteResp.error?.message ?: "Falha ao excluir curso"
                                    }
                                } catch (e: Exception) {
                                    error = e.message
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


