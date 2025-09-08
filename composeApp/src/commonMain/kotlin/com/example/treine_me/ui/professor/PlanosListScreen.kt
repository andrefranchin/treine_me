package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import com.example.treine_me.api.PlanoResponse
import com.example.treine_me.services.PlanoService
import kotlinx.coroutines.launch

@Composable
fun PlanosListScreen(
    onCreate: () -> Unit,
    onEdit: (String) -> Unit
) {
    var planos by remember { mutableStateOf<List<PlanoResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val planoService = remember { PlanoService() }
    val scope = rememberCoroutineScope()

    // Carregar planos ao inicializar
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                loading = true
                val response = planoService.listPlanos()
                if (response.success) {
                    planos = response.data?.data ?: emptyList()
                } else {
                    errorMessage = response.error?.message ?: "Erro ao carregar planos"
                }
            } catch (e: Exception) {
                errorMessage = "Erro: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Planos", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onCreate) { Text("Novo Plano") }
        }
        Spacer(Modifier.height(16.dp))
        
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (planos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        "Nenhum plano encontrado",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Clique em 'Novo Plano' para criar seu primeiro plano",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(planos.size) { index ->
                    val plano = planos[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    plano.nome,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    plano.descricao,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                Row {
                                    Text(
                                        "R$ ${plano.valor}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        plano.recorrencia.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row {
                                IconButton(onClick = { onEdit(plano.id) }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val response = planoService.deletePlano(plano.id)
                                                if (response.success) {
                                                    // Recarregar lista
                                                    val listResponse = planoService.listPlanos()
                                                    if (listResponse.success) {
                                                        planos = listResponse.data?.data ?: emptyList()
                                                    }
                                                } else {
                                                    errorMessage = response.error?.message ?: "Erro ao deletar plano"
                                                }
                                            } catch (e: Exception) {
                                                errorMessage = "Erro: ${e.message}"
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Deletar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}