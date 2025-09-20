package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.example.treine_me.services.ProdutoService
import com.example.treine_me.api.ProdutoResponse
import androidx.compose.material3.HorizontalDivider
import com.example.treine_me.ui.controls.AppNetworkImage
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import com.example.treine_me.ui.components.CourseAvatar

@Composable
fun CursoDetailScreen(
    id: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenModulo: (String) -> Unit
) {
    val produtoService = remember { ProdutoService() }
    var curso by remember { mutableStateOf<ProdutoResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showCreateModulo by remember { mutableStateOf(false) }
    var moduloRefreshSignal by remember { mutableStateOf(0) }

    LaunchedEffect(id) {
        isLoading = true
        errorMessage = null
        try {
            val resp = produtoService.getProduto(id)
            if (resp.success) {
                curso = resp.data
            } else {
                errorMessage = resp.error?.message ?: "Erro ao carregar curso"
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Erro inesperado"
        } finally {
            isLoading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar") }
                curso?.let { c ->
                    Text(c.titulo, style = MaterialTheme.typography.titleLarge)
                } ?: run {
                    Text("Curso", style = MaterialTheme.typography.titleLarge)
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
        }
        Spacer(Modifier.height(16.dp))
        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        if (isLoading) {
            Text("Carregando...")
        } else {
            curso?.let { c ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Course header with avatar and info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CourseAvatar(
                            title = c.titulo,
                            imageUrl = c.capaUrl,
                            size = 80.dp,
                            isCircular = false
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = c.titulo,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = c.descricao,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Metadados
                    HorizontalDivider()
                    // Módulos header com botão de novo módulo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Módulos", style = MaterialTheme.typography.titleMedium)
                        Button(onClick = { showCreateModulo = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Novo módulo")
                        }
                    }

                    // Lista de módulos do curso
                    ModuloList(
                        produtoId = c.id,
                        refreshSignal = moduloRefreshSignal,
                        onOpenModulo = { modulo -> onOpenModulo(modulo.id) }
                    )

                    if (showCreateModulo) {
                        AlertDialog(
                            onDismissRequest = { showCreateModulo = false },
                            confirmButton = {},
                            title = { Text("Novo Módulo") },
                            text = {
                                Box(Modifier.fillMaxWidth().height(420.dp)) {
                                    // Conteúdo tabulado similar ao curso
                                    ModuloTabsScreen(
                                        produtoId = c.id,
                                        onDone = { showCreateModulo = false },
                                        onCreated = { moduloRefreshSignal++ }
                                    )
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showCreateModulo = false }) { Text("Fechar") }
                            }
                        )
                    }
                }
            }
        }
    }
}


