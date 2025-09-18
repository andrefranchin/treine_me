package com.example.treine_me.ui.professor

import androidx.compose.runtime.*
import com.example.treine_me.ui.professor.ModuloDetailScreen
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
                            onEdit = { id -> navigate(ProfessorRoute.CursoEdit(id)) },
                            onOpen = { id -> navigate(ProfessorRoute.CursoDetail(id)) }
                        )
                        is ProfessorRoute.CursoEdit -> CursoTabsScreen(
                            id = route.id,
                            onDone = { navigate(ProfessorRoute.Cursos) }
                        )
                        is ProfessorRoute.CursoDetail -> CursoDetailScreen(
                            id = route.id,
                            onBack = { navigate(ProfessorRoute.Cursos) },
                            onEdit = { navigate(ProfessorRoute.CursoEdit(route.id)) },
                            onOpenModulo = { moduloId -> navigate(ProfessorRoute.ModuloDetail(produtoId = route.id, moduloId = moduloId)) }
                        )
                        is ProfessorRoute.ModuloDetail -> ModuloDetailScreen(
                            produtoId = route.produtoId,
                            moduloId = route.moduloId,
                            onBack = { navigate(ProfessorRoute.Cursos) }
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
