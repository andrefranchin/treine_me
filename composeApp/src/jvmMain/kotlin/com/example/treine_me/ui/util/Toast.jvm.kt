package com.example.treine_me.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import javax.swing.JOptionPane

@Composable
actual fun PlatformToast(message: String) {
    LaunchedEffect(message) {
        JOptionPane.showMessageDialog(null, message)
    }
}


