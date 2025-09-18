package com.example.treine_me.ui.controls

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.PlanoResponse
import com.example.treine_me.services.PlanoService
import kotlinx.coroutines.launch

@Composable
fun PlanoSelect(
    modifier: Modifier = Modifier,
    label: String = "Plano",
    selectedPlanoId: String?,
    onPlanoSelected: (String) -> Unit
) {
    val planoService = remember { PlanoService() }
    var planos by remember { mutableStateOf<List<PlanoResponse>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            val resp = planoService.listPlanos(page = 1, size = 100)
            if (resp.success) {
                planos = resp.data?.data ?: emptyList()
            } else {
                error = resp.error?.message ?: "Falha ao carregar planos"
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    Column(modifier) {
        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Box {
                val current = planos.firstOrNull { it.id == selectedPlanoId }?.nome ?: if (loading) "Carregando..." else "Selecione"
                Button(onClick = { if (!loading) expanded = true }, enabled = !loading) { Text(current) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (loading) {
                        DropdownMenuItem(text = { Row(verticalAlignment = Alignment.CenterVertically) { CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)); Text("Carregando...") } }, onClick = {})
                    } else {
                        planos.forEach { plano ->
                            DropdownMenuItem(
                                text = { Text(plano.nome) },
                                onClick = {
                                    onPlanoSelected(plano.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


