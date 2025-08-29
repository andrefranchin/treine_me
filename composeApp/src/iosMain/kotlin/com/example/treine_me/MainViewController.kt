package com.example.treine_me

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

fun MainViewController() = ComposeUIViewController {
    RoleOnlyScreen()
}

@Composable
private fun RoleOnlyScreen() {
    when (defaultAppRole()) {
        AppRole.ALUNO -> Text("Aluno")
        AppRole.PROFESSOR -> Text("Professor")
        AppRole.ADMIN -> Text("Admin")
    }
}