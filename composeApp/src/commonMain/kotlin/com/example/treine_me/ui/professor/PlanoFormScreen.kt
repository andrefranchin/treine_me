package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import com.example.treine_me.api.Recorrencia
import com.example.treine_me.services.PlanoService
import kotlinx.coroutines.launch

@Composable
fun PlanoFormScreen(
    id: String?,
    onDone: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var recorrencia by remember { mutableStateOf(Recorrencia.MENSAL) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val planoService = remember { PlanoService() }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            if (id == null) "Cadastrar Plano" else "Editar Plano",
            style = MaterialTheme.typography.headlineMedium
        )
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
        
        if (successMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = successMessage!!,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            isError = nome.isBlank() && errorMessage != null
        )
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            modifier = Modifier.fillMaxWidth(),
            isError = valor.isBlank() && errorMessage != null
        )
        Spacer(Modifier.height(8.dp))
        
        // Campo simples para recorrência
        OutlinedTextField(
            value = recorrencia.name,
            onValueChange = { },
            readOnly = true,
            label = { Text("Recorrência") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    recorrencia = if (recorrencia == Recorrencia.MENSAL) Recorrencia.ANUAL else Recorrencia.MENSAL
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Alternar")
                }
            }
        )
        
        Spacer(Modifier.height(24.dp))
        
        Row {
            Button(
                onClick = {
                    if (nome.isBlank() || valor.isBlank()) {
                        errorMessage = "Nome e valor são obrigatórios"
                        return@Button
                    }
                    
                    loading = true
                    errorMessage = null
                    successMessage = null
                    
                    scope.launch {
                        try {
                            if (id == null) {
                                // Criar novo plano
                                val request = com.example.treine_me.api.PlanoCreateRequest(
                                    nome = nome,
                                    descricao = descricao,
                                    valor = valor,
                                    recorrencia = recorrencia
                                )
                                val response = planoService.createPlano(request)
                                
                                if (response.success) {
                                    successMessage = "Plano criado com sucesso!"
                                    // Aguardar um pouco antes de navegar
                                    kotlinx.coroutines.delay(1500)
                                    onDone()
                                } else {
                                    errorMessage = response.error?.message ?: "Erro ao criar plano"
                                }
                            } else {
                                // Atualizar plano existente
                                val request = com.example.treine_me.api.PlanoUpdateRequest(
                                    nome = nome,
                                    descricao = descricao,
                                    valor = valor,
                                    recorrencia = recorrencia
                                )
                                val response = planoService.updatePlano(id, request)
                                
                                if (response.success) {
                                    successMessage = "Plano atualizado com sucesso!"
                                    kotlinx.coroutines.delay(1500)
                                    onDone()
                                } else {
                                    errorMessage = response.error?.message ?: "Erro ao atualizar plano"
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = "Erro: ${e.message}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (loading) "Salvando..." else "Salvar")
            }
            
            Spacer(Modifier.width(8.dp))
            
            Button(
                onClick = onDone,
                enabled = !loading,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
        }
    }
}
