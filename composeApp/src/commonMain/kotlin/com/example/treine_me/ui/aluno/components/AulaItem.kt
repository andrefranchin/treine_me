package com.example.treine_me.ui.aluno.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AulaItem(
    aula: com.example.treine_me.repository.PublicAulaResponse,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status da aula
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (isCompleted) Color(0xFF10B981) else Color(0xFFE2E8F0),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Icon(
                    when (aula.tipoConteudo.name) {
                        "VIDEO" -> Icons.Default.PlayArrow
                        "TEXTO" -> Icons.AutoMirrored.Filled.Article
                        "ATIVIDADE" -> Icons.AutoMirrored.Filled.Assignment
                        else -> Icons.Default.Circle
                    },
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = aula.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isCompleted) Color(0xFF64748B) else Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = aula.tipoConteudo.name.lowercase().replaceFirstChar { char -> char.uppercase() },
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                
                if (aula.temConteudo) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                }
            }
        }
        
        if (!aula.temConteudo) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Conteúdo bloqueado",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        } else {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


