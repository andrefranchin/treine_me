package com.example.treine_me.ui.professor

import androidx.compose.runtime.*
import com.example.treine_me.ui.feedback.LoadingHost
import com.example.treine_me.ui.professor.ProfessorLoginScreen

@Composable
fun ProfessorRoot() {
    var loggedIn by remember { mutableStateOf(false) }
    LoadingHost {
        if (!loggedIn) {
            ProfessorLoginScreen(onLoggedIn = { loggedIn = true })
        } else {
            ProfessorScaffold(
                onLogout = { loggedIn = false },
                content = { route, navigate ->
                    when (route) {
                        is ProfessorRoute.Cursos -> CursosListScreen(
                            onCreate = { navigate(ProfessorRoute.CursoEdit(null)) },
                            onEdit = { id -> navigate(ProfessorRoute.CursoEdit(id)) }
                        )
                        is ProfessorRoute.CursoEdit -> CursoFormScreen(
                            id = route.id,
                            onDone = { navigate(ProfessorRoute.Cursos) }
                        )
                        is ProfessorRoute.Alunos -> AlunosListScreen()
                        is ProfessorRoute.Planos -> PlanosListScreen(
                            onCreate = { navigate(ProfessorRoute.PlanoEdit(null)) },
                            onEdit = { id -> navigate(ProfessorRoute.PlanoEdit(id)) }
                        )
                        is ProfessorRoute.PlanoEdit -> PlanoFormScreen(
                            id = route.id,
                            onDone = { navigate(ProfessorRoute.Planos) }
                        )
                    }
                }
            )
        }
    }
}
