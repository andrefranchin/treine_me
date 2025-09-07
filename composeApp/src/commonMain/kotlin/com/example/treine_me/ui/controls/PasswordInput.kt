package com.example.treine_me.ui.controls

import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun AppPasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    val (visible, setVisible) = remember { mutableStateOf(false) }
    AppTextInput(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        leadingIconName = "lock",
        trailingIcon = {
            IconButton(onClick = { setVisible(!visible) }) {
                AppIcon(name = if (visible) "visibility_off" else "visibility", contentDescription = "alternar visibilidade")
            }
        },
        isError = isError,
        supportingText = supportingText,
        modifier = modifier,
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    )
}


