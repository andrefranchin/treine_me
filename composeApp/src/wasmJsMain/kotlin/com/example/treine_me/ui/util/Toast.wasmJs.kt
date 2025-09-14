package com.example.treine_me.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.window

@Composable
actual fun PlatformToast(message: String) {
    LaunchedEffect(message) {
        window.alert(message)
    }
}


