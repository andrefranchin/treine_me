package com.example.treine_me

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val roleText = when (defaultAppRole()) {
        AppRole.ALUNO -> "Aluno"
        AppRole.PROFESSOR -> "Professor"
        AppRole.ADMIN -> "Admin"
    }
    Text(roleText)
}