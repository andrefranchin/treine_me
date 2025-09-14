package com.example.treine_me.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContentColor: Color = LocalContentColor.current.copy(alpha = 0.6f),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    val contentColor = if (selected) selectedContentColor else unselectedContentColor

    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        color = Color.Transparent,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            content = content
        )
    }
}
