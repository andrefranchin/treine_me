package com.example.treine_me.ui.controls


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier

// Não há erros de compilação neste código.
@Composable
fun Dialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    // O parâmetro icon não estava sendo usado, agora está.
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = dialogTitle) // Usando dialogTitle para acessibilidade
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    iconName: String? = null,
    title: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = {
            if (iconName != null) {
                AppIcon(name = iconName, contentDescription = null)
            }
        },
        title = {
            if (title != null) title()
        },
        text = {
            content()
        },
        confirmButton = {
            if (confirmButton != null) {
                confirmButton()
            } else {
                TextButton(onClick = onDismissRequest) { Text("OK") }
            }
        },
        dismissButton = {
            if (dismissButton != null) dismissButton()
        }
    )
}
