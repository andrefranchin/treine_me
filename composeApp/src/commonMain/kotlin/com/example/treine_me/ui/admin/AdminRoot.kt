package com.example.treine_me.ui.admin

import androidx.compose.runtime.*
import com.example.treine_me.api.ProfessorResponse
import com.example.treine_me.repository.AdminProfessoresRepository
import com.example.treine_me.storage.InMemoryStore
import com.example.treine_me.ui.feedback.LoadingHost

@Composable
fun AdminRoot() {
    var loggedIn by remember { mutableStateOf(false) }

    val professoresStore = remember { InMemoryStore<ProfessorResponse> { it.id } }
    val professoresRepo = remember { AdminProfessoresRepository(professoresStore) }

    LoadingHost {
        if (!loggedIn) {
            AdminLoginScreen(onLoggedIn = { loggedIn = true })
        } else {
            AdminScaffold(
                onLogout = { loggedIn = false },
                content = { route, navigate ->
                    when (route) {
                        is AdminRoute.Professores -> ProfessoresListScreen(
                            repo = professoresRepo,
                            onCreate = { navigate(AdminRoute.ProfessorEdit(null)) },
                            onEdit = { id -> navigate(AdminRoute.ProfessorEdit(id)) }
                        )
                        is AdminRoute.ProfessorEdit -> ProfessorFormScreen(
                            repo = professoresRepo,
                            id = route.id,
                            onDone = { navigate(AdminRoute.Professores) }
                        )
                    }
                }
            )
        }
    }
}


