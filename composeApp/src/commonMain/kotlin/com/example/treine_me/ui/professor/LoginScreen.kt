package com.example.treine_me.ui.professor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.network.AuthService
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import com.example.treine_me.ui.controls.AppImage

@Composable
fun ProfessorLoginScreen(
    authService: AuthService = AuthService(),
    onLoggedIn: () -> Unit
) {
    var email by remember { mutableStateOf("dedefranchin@gmail.com") }
    var senha by remember { mutableStateOf("bebaleite") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        // Background com gradiente escuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E2E2E),
                            Color(0xFF1A1A1A),
                            Color(0xFF0F0F0F)
                        )
                    )
                )
        )
        // Overlay escuro sutil
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
        // Card de login centralizado
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .widthIn(min = 320.dp, max = 400.dp)
                    .padding(32.dp)
                    .background(
                        Color.White.copy(alpha = 0.95f), 
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Bem-vindo, Professor(a)!", 
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF2E7D32)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Acesse sua área para gerenciar seus planos e alunos.", 
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, 
                    color = Color(0xFF666666)
                )
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(20.dp))
                Button(enabled = !loading, onClick = {
                    loading = true
                    error = null
                    scope.launch {
                        try {
                            authService.loginProfessor(email, senha)
                            onLoggedIn()
                        } catch (t: Throwable) {
                            error = t.message
                        } finally {
                            loading = false
                        }
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        Color(0xFF2E7D32),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )) {
                    Text(
                        if (loading) "Entrando..." else "Entrar",
                        color = Color.White,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                }
                if (!error.isNullOrBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(error!!, color = Color.Red)
                }
            }
        }
    }
}
