package com.example.treine_me.ui.controls

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class ButtonIconVariant { Primary, Secondary, Tertiary }

@Composable
fun ButtonIcon(
    iconName: String,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    variant: ButtonIconVariant = ButtonIconVariant.Primary,
    enabled: Boolean = true,
) {
    val colors = when (variant) {
        ButtonIconVariant.Primary -> ButtonDefaults.buttonColors()
        ButtonIconVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
        )
        ButtonIconVariant.Tertiary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
        )
    }

    if (text == null) {
        IconButton(onClick = onClick, enabled = enabled, modifier = modifier) {
            AppIcon(name = iconName, contentDescription = contentDescription)
        }
    } else {
        Button(onClick = onClick, enabled = enabled, colors = colors, modifier = modifier) {
            AppIcon(name = iconName, contentDescription = contentDescription)
            Text(" \u00A0$text")
        }
    }
}


