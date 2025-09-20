package com.example.treine_me.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treine_me.ui.controls.AppNetworkImage

@Composable
fun CourseAvatar(
    title: String,
    imageUrl: String? = null,
    size: Dp = 56.dp,
    isCircular: Boolean = true,
    modifier: Modifier = Modifier
) {
    val shape = if (isCircular) CircleShape else RoundedCornerShape(8.dp)
    
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                if (imageUrl == null) MaterialTheme.colorScheme.primary 
                else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AppNetworkImage(
                url = imageUrl,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Capa do curso $title",
                contentScale = ContentScale.Crop
            )
        } else {
            // Show initials if no image
            val initials = title.split(" ")
                .take(2)
                .map { it.firstOrNull()?.uppercase() ?: "" }
                .joinToString("")
                .take(2)
            
            Text(
                text = initials,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = when {
                    size <= 32.dp -> 12.sp
                    size <= 48.dp -> 14.sp
                    size <= 64.dp -> 16.sp
                    else -> 18.sp
                }
            )
        }
    }
}
