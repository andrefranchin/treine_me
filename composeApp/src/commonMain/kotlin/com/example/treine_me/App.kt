package com.example.treine_me

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.treine_me.ui.admin.AdminRoot
import com.example.treine_me.ui.professor.ProfessorRoot

@Composable
@Preview
fun App() {
    when (defaultAppRole()) {
        AppRole.ALUNO -> Text("Aluno")
        AppRole.PROFESSOR -> ProfessorRoot()
        AppRole.ADMIN -> AdminRoot()
    }
}