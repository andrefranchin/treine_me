package com.example.treine_me

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.treine_me.ui.admin.AdminRoot
import com.example.treine_me.ui.professor.ProfessorRoot
import com.example.treine_me.ui.aluno.AlunoRoot

@Composable
@Preview
fun App() {
    when (defaultAppRole()) {
        AppRole.ALUNO -> AlunoRoot()
        AppRole.PROFESSOR -> ProfessorRoot()
        AppRole.ADMIN -> AdminRoot()
    }
}