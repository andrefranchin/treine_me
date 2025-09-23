package com.example.treine_me.ui.professor.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treine_me.ui.controls.AppNetworkImage

@Composable
fun AlunoFormPreview(
    nome: String,
    email: String,
    fotoPerfilUrl: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pré-visualização",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (fotoPerfilUrl.isBlank()) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                } else {
                    AppNetworkImage(
                        url = fotoPerfilUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nome
            Text(
                text = if (nome.isBlank()) "Nome do aluno" else nome,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (nome.isBlank()) 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) 
                else 
                    MaterialTheme.colorScheme.onSurface
            )

            // Email
            Text(
                text = if (email.isBlank()) "email@exemplo.com" else email,
                fontSize = 14.sp,
                color = if (email.isBlank()) 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
