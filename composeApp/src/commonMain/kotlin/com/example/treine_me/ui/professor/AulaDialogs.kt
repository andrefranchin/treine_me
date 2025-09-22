package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.AulaResponse
import com.example.treine_me.api.TipoConteudo
import com.example.treine_me.api.DificuldadeTreino
import com.example.treine_me.api.TipoTreino
import com.example.treine_me.ui.controls.PlanoSelect

data class AulaFormData(
    val titulo: String,
    val descricao: String,
    val tipoConteudo: TipoConteudo,
    val planoId: String,
    val caloriasPerdidas: Int? = null,
    val dificuldade: DificuldadeTreino? = null,
    val tipoTreino: TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null,
    val observacoesTreino: String? = null
)

@Composable
fun AulaCreateDialog(
    onDismiss: () -> Unit,
    onSave: (AulaFormData) -> Unit
) {
    AulaFormDialog(
        title = "Nova aula",
        initial = null,
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@Composable
fun AulaEditDialog(
    initial: AulaResponse,
    onDismiss: () -> Unit,
    onSave: (AulaFormData) -> Unit
) {
    AulaFormDialog(
        title = "Editar aula",
        initial = initial,
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@Composable
private fun AulaFormDialog(
    title: String,
    initial: AulaResponse?,
    onDismiss: () -> Unit,
    onSave: (AulaFormData) -> Unit
) {
    var titulo by remember { mutableStateOf(initial?.titulo ?: "") }
    var descricao by remember { mutableStateOf(initial?.descricao ?: "") }
    var planoId by remember { mutableStateOf(initial?.planoId ?: "") }
    var tipo by remember { mutableStateOf(initial?.tipoConteudo ?: TipoConteudo.VIDEO) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Campos de configuração do treino
    var caloriasPerdidas by remember { mutableStateOf(initial?.caloriasPerdidas?.toString() ?: "") }
    var dificuldade by remember { mutableStateOf(initial?.dificuldade) }
    var tipoTreino by remember { mutableStateOf(initial?.tipoTreino) }
    var equipamentosNecessarios by remember { mutableStateOf(initial?.equipamentosNecessarios ?: "") }
    var duracaoTreinoMinutos by remember { mutableStateOf(initial?.duracaoTreinoMinutos?.toString() ?: "") }
    var intensidade by remember { mutableStateOf(initial?.intensidade?.toString() ?: "") }
    var observacoesTreino by remember { mutableStateOf(initial?.observacoesTreino ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(500.dp) // Altura fixa para o diálogo
                    .verticalScroll(rememberScrollState())
            ) {
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }
                
                // ========== CAMPOS BÁSICOS ==========
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(Modifier.height(8.dp))
                
                PlanoSelect(
                    selectedPlanoId = planoId.ifBlank { null },
                    onPlanoSelected = { planoId = it }
                )
                Spacer(Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tipo de Conteúdo:")
                    Spacer(Modifier.width(8.dp))
                    DropdownMenuWrapper(current = tipo, onChange = { tipo = it })
                }
                Spacer(Modifier.height(16.dp))
                
                // ========== CONFIGURAÇÕES DO TREINO ==========
                Text("Configurações do Treino", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = caloriasPerdidas,
                    onValueChange = { caloriasPerdidas = it },
                    label = { Text("Calorias Perdidas") },
                    placeholder = { Text("Ex: 300") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Dificuldade:")
                    Spacer(Modifier.width(8.dp))
                    DificuldadeDropdown(current = dificuldade, onChange = { dificuldade = it })
                }
                Spacer(Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tipo de Treino:")
                    Spacer(Modifier.width(8.dp))
                    TipoTreinoDropdown(current = tipoTreino, onChange = { tipoTreino = it })
                }
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = equipamentosNecessarios,
                    onValueChange = { equipamentosNecessarios = it },
                    label = { Text("Equipamentos Necessários") },
                    placeholder = { Text("Ex: Halteres de 2kg, tapete de yoga") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = duracaoTreinoMinutos,
                    onValueChange = { duracaoTreinoMinutos = it },
                    label = { Text("Duração do Treino (min)") },
                    placeholder = { Text("Ex: 45") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = intensidade,
                    onValueChange = { intensidade = it },
                    label = { Text("Intensidade (1-10)") },
                    placeholder = { Text("Ex: 7") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = observacoesTreino,
                    onValueChange = { observacoesTreino = it },
                    label = { Text("Observações do Treino") },
                    placeholder = { Text("Ex: Faça pausas se necessário") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (planoId.isBlank()) {
                    error = "Selecione um plano"
                    return@Button
                }
                
                // Validar campos numéricos
                val caloriasNum = caloriasPerdidas.toIntOrNull()
                val duracaoNum = duracaoTreinoMinutos.toIntOrNull()
                val intensidadeNum = intensidade.toIntOrNull()
                
                if (intensidadeNum != null && (intensidadeNum < 1 || intensidadeNum > 10)) {
                    error = "Intensidade deve estar entre 1 e 10"
                    return@Button
                }
                
                val formData = AulaFormData(
                    titulo = titulo,
                    descricao = descricao,
                    tipoConteudo = tipo,
                    planoId = planoId,
                    caloriasPerdidas = caloriasNum,
                    dificuldade = dificuldade,
                    tipoTreino = tipoTreino,
                    equipamentosNecessarios = equipamentosNecessarios.ifBlank { null },
                    duracaoTreinoMinutos = duracaoNum,
                    intensidade = intensidadeNum,
                    observacoesTreino = observacoesTreino.ifBlank { null }
                )
                
                onSave(formData)
            }) { Text("Salvar") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun DropdownMenuWrapper(current: TipoConteudo, onChange: (TipoConteudo) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) { Text(current.name) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TipoConteudo.values().forEach { tc ->
                DropdownMenuItem(
                    text = { Text(tc.name) },
                    onClick = {
                        onChange(tc)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DificuldadeDropdown(current: DificuldadeTreino?, onChange: (DificuldadeTreino?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) { 
            Text(current?.name ?: "Selecionar Dificuldade") 
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Nenhuma") },
                onClick = {
                    onChange(null)
                    expanded = false
                }
            )
            DificuldadeTreino.values().forEach { dif ->
                DropdownMenuItem(
                    text = { Text(dif.name) },
                    onClick = {
                        onChange(dif)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TipoTreinoDropdown(current: TipoTreino?, onChange: (TipoTreino?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) { 
            Text(current?.name ?: "Selecionar Tipo") 
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Nenhum") },
                onClick = {
                    onChange(null)
                    expanded = false
                }
            )
            TipoTreino.values().forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo.name) },
                    onClick = {
                        onChange(tipo)
                        expanded = false
                    }
                )
            }
        }
    }
}


