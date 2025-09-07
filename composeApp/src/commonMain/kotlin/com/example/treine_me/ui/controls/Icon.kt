package com.example.treine_me.ui.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

object IconRegistry {
    private val icons: Map<String, ImageVector> = mapOf(
        // Base/common icons
        "add" to Icons.Filled.Add,
        "arrow_back" to Icons.Filled.ArrowBack,
        "arrow_forward" to Icons.Filled.ArrowForward,
        "check" to Icons.Filled.Check,
        "close" to Icons.Filled.Close,
        "delete" to Icons.Filled.Delete,
        "done" to Icons.Filled.Done,
        "edit" to Icons.Filled.Edit,
        "email" to Icons.Filled.Email,
        "error" to Icons.Filled.Error,
        "info" to Icons.Filled.Info,
        "menu" to Icons.Filled.Menu,
        "lock" to Icons.Filled.Lock,
        "person" to Icons.Filled.Person,
        "search" to Icons.Filled.Search,
        "visibility" to Icons.Filled.Visibility,
        "visibility_off" to Icons.Filled.VisibilityOff,
    )

    fun get(name: String): ImageVector? = icons[name]
    fun has(name: String): Boolean = icons.containsKey(name)
}

@Composable
fun AppIcon(
    name: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val vector: ImageVector? = IconRegistry.get(name)
    if (vector != null) {
        Icon(imageVector = vector, contentDescription = contentDescription, modifier = modifier)
    } else {
        // Fallback: use info icon when missing to force us to register new ones
        Icon(imageVector = Icons.Filled.Info, contentDescription = contentDescription, modifier = modifier)
    }
}


