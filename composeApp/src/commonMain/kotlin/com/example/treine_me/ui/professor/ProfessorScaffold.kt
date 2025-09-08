package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import com.example.treine_me.ui.controls.ButtonIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorScaffold(
    onLogout: () -> Unit,
    content: @Composable (ProfessorRoute, (ProfessorRoute) -> Unit) -> Unit
) {
    var route by remember { mutableStateOf<ProfessorRoute>(ProfessorRoute.Cursos) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text("Cursos") },
                    selected = route is ProfessorRoute.Cursos,
                    onClick = {
                        route = ProfessorRoute.Cursos
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Alunos") },
                    selected = route is ProfessorRoute.Alunos,
                    onClick = {
                        route = ProfessorRoute.Alunos
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Planos") },
                    selected = route is ProfessorRoute.Planos,
                    onClick = {
                        route = ProfessorRoute.Planos
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Sair") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Professor") },
                navigationIcon = {
                    ButtonIcon(
                        iconName = "menu",
                        contentDescription = "Abrir menu",
                        onClick = { scope.launch { drawerState.open() } }
                    )
                }
            )
        }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content(route) { route = it }
            }
        }
    }
}
