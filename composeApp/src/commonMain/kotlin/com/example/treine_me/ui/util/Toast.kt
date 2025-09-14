package com.example.treine_me.ui.util

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformToast(message: String)

@Composable
fun ShowToast(message: String) {
    PlatformToast(message)
}


