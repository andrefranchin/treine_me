package com.example.treine_me.ui.professor.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlunoFormFields(
    nome: String,
    onNomeChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    senha: String,
    onSenhaChange: (String) -> Unit,
    fotoPerfilUrl: String,
    onFotoPerfilUrlChange: (String) -> Unit,
    isEditMode: Boolean = false,
    nomeError: String? = null,
    emailError: String? = null,
    senhaError: String? = null,
    modifier: Modifier = Modifier
) {
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo Nome
        OutlinedTextField(
            value = nome,
            onValueChange = onNomeChange,
            label = { Text("Nome completo") },
            placeholder = { Text("Digite o nome do aluno") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = nomeError != null,
            supportingText = nomeError?.let { { Text(it) } },
            shape = RoundedCornerShape(12.dp)
        )

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            placeholder = { Text("Digite o email do aluno") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } },
            shape = RoundedCornerShape(12.dp),
            enabled = !isEditMode // Email não pode ser editado
        )

        // Campo Senha (apenas no modo criação)
        if (!isEditMode) {
            OutlinedTextField(
                value = senha,
                onValueChange = onSenhaChange,
                label = { Text("Senha") },
                placeholder = { Text("Digite a senha inicial") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                },
                isError = senhaError != null,
                supportingText = senhaError?.let { { Text(it) } } ?: { Text("Mínimo 6 caracteres") },
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Campo URL da Foto (opcional)
        OutlinedTextField(
            value = fotoPerfilUrl,
            onValueChange = onFotoPerfilUrlChange,
            label = { Text("URL da foto (opcional)") },
            placeholder = { Text("https://exemplo.com/foto.jpg") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
