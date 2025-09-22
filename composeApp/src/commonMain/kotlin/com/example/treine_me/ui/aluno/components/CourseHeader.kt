package com.example.treine_me.ui.aluno.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treine_me.api.ProdutoResponse

@Composable
fun CourseHeader(produto: ProdutoResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            when (produto.tipo.name) {
                                "CURSO" -> Color(0xFF10B981).copy(alpha = 0.1f)
                                "MENTORIA" -> Color(0xFF8B5CF6).copy(alpha = 0.1f)
                                "EBOOK" -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                                else -> Color(0xFF6366F1).copy(alpha = 0.1f)
                            },
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
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
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = produto.titulo,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = produto.descricao,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF6366F1).copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = produto.tipo.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6366F1)
                            )
                        }
                    }
                }
            }
        }
    }
}
