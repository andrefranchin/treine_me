package com.example.treine_me.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.treine_me.ui.controls.AppPasswordInput
import com.example.treine_me.ui.controls.AppTextInput

data class ProfessorFormState(
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
)

@Composable
fun ProfessorForm(
    onChange: (ProfessorFormState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (state, setState) = remember { mutableStateOf(ProfessorFormState()) }
    fun update(update: ProfessorFormState.() -> ProfessorFormState) {
        val newState = state.update()
        setState(newState)
        onChange(newState)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Novo Professor")
        Spacer(Modifier.height(12.dp))
        AppTextInput(
            value = state.nome,
            onValueChange = { value -> update { copy(nome = value) } },
            label = "Nome",
            leadingIconName = "person",
            placeholder = "Nome completo",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        AppTextInput(
            value = state.email,
            onValueChange = { value -> update { copy(email = value) } },
            label = "Email",
            leadingIconName = "email",
            placeholder = "email@provedor.com",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        AppPasswordInput(
            value = state.senha,
            onValueChange = { value -> update { copy(senha = value) } },
            label = "Senha",
            placeholder = "Digite a senha",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}


