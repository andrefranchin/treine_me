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

@Composable
fun CursoDetailScreen(
    id: String,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val produtoService = remember { ProdutoService() }
    var curso by remember { mutableStateOf<ProdutoResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
                Text("Curso", style = MaterialTheme.typography.titleLarge)
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
                    // Capa
                    c.capaUrl?.let { coverUrl ->
                        AppNetworkImage(
                            url = coverUrl,
                            modifier = Modifier.height(160.dp).fillMaxWidth(),
                            contentDescription = "Capa do curso"
                        )
                    }
                    // Título e descrição
                    Text(c.titulo.trim(), style = MaterialTheme.typography.headlineSmall)
                    Text(c.descricao, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    // Metadados
                    HorizontalDivider()
                    // Placeholder para módulos
                    Text("Módulos", style = MaterialTheme.typography.titleMedium)
                    Text("(Em breve) Listar e gerenciar módulos deste curso.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}


