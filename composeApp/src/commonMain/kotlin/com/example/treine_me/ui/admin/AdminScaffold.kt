package com.example.treine_me.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import com.example.treine_me.ui.controls.ButtonIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    onLogout: () -> Unit,
    content: @Composable (AdminRoute, (AdminRoute) -> Unit) -> Unit
) {
    var route by remember { mutableStateOf<AdminRoute>(AdminRoute.Professores) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text("Professores") },
                    selected = route is AdminRoute.Professores,
                    onClick = {
                        route = AdminRoute.Professores
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
                title = { Text("Admin") },
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


