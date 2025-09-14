package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ProdutoCreateRequest
import com.example.treine_me.api.ProdutoResponse
import com.example.treine_me.api.TipoProduto
import com.example.treine_me.network.ApiException
import com.example.treine_me.services.ProdutoService
import kotlinx.coroutines.launch

@Composable
fun CursoFormScreen(
    id: String?,
    onDone: () -> Unit,
    cursoCover: String? = null,
    onCreated: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val produtoService = remember { ProdutoService() }
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        if (errorMessage != null) {
            Text(errorMessage!!, color = androidx.compose.ui.graphics.Color.Red)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
        
        // Mostrar capa selecionada se houver
        if (cursoCover != null) {
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Capa do Curso",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Capa selecionada na aba Personalização",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Row {
            Button(
                enabled = !isSaving,
                onClick = {
                    errorMessage = null
                    if (id == null) {
                        // Create
                        scope.launch {
                            isSaving = true
                            try {
                                val req = ProdutoCreateRequest(
                                    titulo = titulo,
                                    descricao = descricao,
                                    tipo = TipoProduto.CURSO,
                                    capaUrl = cursoCover
                                )
                                val resp = produtoService.createProduto(req)
                                if (resp.success) {
                                    resp.data?.let { created ->
                                        onCreated(created.id)
                                    } ?: run {
                                        errorMessage = resp.error?.message ?: "Falha ao criar curso"
                                    }
                                } else {
                                    errorMessage = resp.error?.message ?: "Falha ao criar curso"
                                }
                            } catch (e: ApiException) {
                                errorMessage = e.message
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Erro inesperado"
                            } finally {
                                isSaving = false
                            }
                        }
                    } else {
                        // Update (futuro)
                        onDone()
                    }
                }
            ) { if (isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Salvar") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onDone) { Text("Cancelar") }
        }
    }
}
