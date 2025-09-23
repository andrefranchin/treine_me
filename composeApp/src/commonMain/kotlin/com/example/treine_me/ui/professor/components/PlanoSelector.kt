package com.example.treine_me.ui.professor.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treine_me.api.PlanoResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanoSelector(
    planos: List<PlanoResponse>,
    selectedPlanoId: String?,
    onPlanoSelected: (String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPlano = planos.find { it.id == selectedPlanoId }

    Column(modifier = modifier) {
        // Label
        Text(
            text = "Plano *",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Selector
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded && !isLoading }
        ) {
            OutlinedTextField(
                value = selectedPlano?.nome ?: "",
                onValueChange = { },
                readOnly = true,
                placeholder = { Text("Selecione um plano") },
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Expandir"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                isError = error != null,
                supportingText = error?.let { { Text(it) } }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (planos.isEmpty() && !isLoading) {
                    DropdownMenuItem(
                        text = { Text("Nenhum plano disponível") },
                        onClick = { },
                        enabled = false
                    )
                } else {
                    planos.forEach { plano ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = plano.nome,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (plano.id == selectedPlanoId) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selecionado",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    
                                    Text(
                                        text = "R$ ${plano.valor} / ${plano.recorrencia.name.lowercase()}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    if (plano.descricao.isNotBlank()) {
                                        Text(
                                            text = plano.descricao,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
                                        )
                                    }
                                }
                            },
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
