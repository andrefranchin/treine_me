package com.example.treine_me.ui.util

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun PlatformToast(message: String) {
    val context = LocalContext.current
    LaunchedEffect(message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}


