package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.api.ModuloResponse
import com.example.treine_me.ui.controls.AppNetworkImage

@Composable
fun ModuloCard(
    modulo: ModuloResponse,
    modifier: Modifier = Modifier,
    onClick: (ModuloResponse) -> Unit = {},
    onEdit: (ModuloResponse) -> Unit = {},
    onDelete: (ModuloResponse) -> Unit = {}
) {
    Card(modifier = modifier.fillMaxWidth().clickable { onClick(modulo) }) {
        Column(modifier = Modifier.padding(12.dp)) {
            val capaUrl = modulo.capaUrl
            if (capaUrl != null) {
                AppNetworkImage(
                    url = capaUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentDescription = "Capa do módulo"
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = modulo.titulo, style = MaterialTheme.typography.titleMedium)
                Row {
                    IconButton(onClick = { onEdit(modulo) }) { Icon(Icons.Default.Edit, contentDescription = "Editar módulo") }
                    IconButton(onClick = { onDelete(modulo) }) { Icon(Icons.Default.Delete, contentDescription = "Excluir módulo") }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = modulo.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


