package com.example.treine_me.ui.aluno.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.treine_me.api.ProdutoResponse

@Composable
fun ProdutoCard(
    produto: ProdutoResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem da capa ou ícone do tipo de produto
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        when (produto.tipo.name) {
                            "CURSO" -> Color(0xFF10B981).copy(alpha = 0.1f)
                            "MENTORIA" -> Color(0xFF8B5CF6).copy(alpha = 0.1f)
                            "EBOOK" -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                            else -> Color(0xFF6366F1).copy(alpha = 0.1f)
                        },
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val capaUrl = produto.capaUrl
                if (capaUrl != null && capaUrl.isNotEmpty()) {
                    // Exibe a imagem da capa
                    AsyncImage(
                        model = capaUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                    )
                    
                    // Ícone de play sobreposto
                    Icon(
                        Icons.Default.PlayCircleOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                Color.Black.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                    )
                } else {
                    // Fallback para ícone quando não há imagem
                    Icon(
                        when (produto.tipo.name) {
                            "CURSO" -> Icons.Default.PlayCircleOutline
                            "MENTORIA" -> Icons.Default.Person
                            "EBOOK" -> Icons.AutoMirrored.Filled.MenuBook
                            else -> Icons.Default.School
                        },
                        contentDescription = null,
                        tint = when (produto.tipo.name) {
                            "CURSO" -> Color(0xFF10B981)
                            "MENTORIA" -> Color(0xFF8B5CF6)
                            "EBOOK" -> Color(0xFFF59E0B)
                            else -> Color(0xFF6366F1)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = produto.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = produto.descricao,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = produto.tipo.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


