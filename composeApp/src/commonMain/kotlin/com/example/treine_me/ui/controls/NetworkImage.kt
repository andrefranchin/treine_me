package com.example.treine_me.ui.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun AppNetworkImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}


