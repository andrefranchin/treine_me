package com.example.treine_me.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.network.AuthService
import com.example.treine_me.network.TokenStore
import kotlinx.coroutines.launch

@Composable
fun AdminLoginScreen(
    authService: AuthService = AuthService(),
    onLoggedIn: () -> Unit
) {
    var email by remember { mutableStateOf("dedefranchin@gmail.com") }
    var senha by remember { mutableStateOf("bebaleite") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(Modifier.width(360.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Login Admin")
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Button(enabled = !loading, onClick = {
                loading = true
                error = null
                scope.launch {
                    try {
                        val res = authService.login(email, senha)
                        TokenStore.token = res.token
                        onLoggedIn()
                    } catch (t: Throwable) {
                        error = t.message
                    } finally {
                        loading = false
                    }
                }
            }) { Text(if (loading) "Entrando..." else "Entrar") }
            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(error!!)
            }
        }
    }
}


