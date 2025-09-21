package com.example.treine_me.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

/**
 * Componente de editor de texto rico usando Compose Rich Editor
 * 
 * @param value Conteúdo HTML atual
 * @param onValueChange Callback quando o conteúdo muda
 * @param label Label do campo
 * @param placeholder Placeholder do campo
 * @param modifier Modifier do componente
 * @param enabled Se o componente está habilitado
 * @param minLines Número mínimo de linhas
 * @param maxLines Número máximo de linhas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRichTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minLines: Int = 6,
    maxLines: Int = 12
) {
    val richTextState = rememberRichTextState()
    
    // Sincronizar valor inicial e mudanças externas
    LaunchedEffect(value) {
        if (richTextState.toHtml() != value) {
            richTextState.setHtml(value)
        }
    }
    
    // Notificar mudanças
    LaunchedEffect(richTextState.toHtml()) {
        val currentHtml = richTextState.toHtml()
        if (currentHtml != value) {
            onValueChange(currentHtml)
        }
    }

    Column(modifier = modifier) {
        // Label
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Toolbar de formatação
        RichTextToolbar(
            richTextState = richTextState,
            enabled = enabled
        )
        
        Spacer(Modifier.height(8.dp))

        // Editor de texto
        RichTextEditor(
            state = richTextState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = (minLines * 24).dp, max = (maxLines * 24).dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(4.dp)
                )
                .padding(12.dp),
            enabled = enabled,
            placeholder = {
                if (placeholder != null) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            },
            colors = RichTextEditorDefaults.richTextEditorColors(
                containerColor = Color.Transparent,
                textColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun RichTextToolbar(
    richTextState: com.mohamedrejeb.richeditor.model.RichTextState,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(getToolbarItems()) { item ->
                ToolbarButton(
                    icon = item.icon,
                    contentDescription = item.description,
                    isActive = when (item.type) {
                        ToolbarItemType.Bold -> richTextState.currentSpanStyle.fontWeight == FontWeight.Bold
                        ToolbarItemType.Italic -> richTextState.currentSpanStyle.fontStyle == FontStyle.Italic
                        ToolbarItemType.Underline -> richTextState.currentSpanStyle.textDecoration != null
                        ToolbarItemType.AlignLeft -> richTextState.currentParagraphStyle.textAlign == TextAlign.Start
                        ToolbarItemType.AlignCenter -> richTextState.currentParagraphStyle.textAlign == TextAlign.Center
                        ToolbarItemType.AlignRight -> richTextState.currentParagraphStyle.textAlign == TextAlign.End
                        ToolbarItemType.OrderedList -> richTextState.isOrderedList
                        ToolbarItemType.UnorderedList -> richTextState.isUnorderedList
                        ToolbarItemType.Code -> richTextState.isCodeSpan
                        else -> false
                    },
                    enabled = enabled,
                    onClick = {
                        when (item.type) {
                            ToolbarItemType.Bold -> richTextState.toggleSpanStyle(
                                androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)
                            )
                            ToolbarItemType.Italic -> richTextState.toggleSpanStyle(
                                androidx.compose.ui.text.SpanStyle(fontStyle = FontStyle.Italic)
                            )
                            ToolbarItemType.Underline -> richTextState.toggleSpanStyle(
                                androidx.compose.ui.text.SpanStyle(
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                )
                            )
                            ToolbarItemType.AlignLeft -> richTextState.toggleParagraphStyle(
                                androidx.compose.ui.text.ParagraphStyle(textAlign = TextAlign.Start)
                            )
                            ToolbarItemType.AlignCenter -> richTextState.toggleParagraphStyle(
                                androidx.compose.ui.text.ParagraphStyle(textAlign = TextAlign.Center)
                            )
                            ToolbarItemType.AlignRight -> richTextState.toggleParagraphStyle(
                                androidx.compose.ui.text.ParagraphStyle(textAlign = TextAlign.End)
                            )
                            ToolbarItemType.OrderedList -> richTextState.toggleOrderedList()
                            ToolbarItemType.UnorderedList -> richTextState.toggleUnorderedList()
                            ToolbarItemType.Code -> richTextState.toggleCodeSpan()
                            ToolbarItemType.Link -> {
                                // TODO: Implementar dialog para adicionar link
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ToolbarButton(
    icon: ImageVector,
    contentDescription: String,
    isActive: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(40.dp)
            .background(
                color = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) 
                       else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

private data class ToolbarItem(
    val type: ToolbarItemType,
    val icon: ImageVector,
    val description: String
)

private enum class ToolbarItemType {
    Bold,
    Italic,
    Underline,
    AlignLeft,
    AlignCenter,
    AlignRight,
    OrderedList,
    UnorderedList,
    Code,
    Link
}

private fun getToolbarItems(): List<ToolbarItem> = listOf(
    ToolbarItem(ToolbarItemType.Bold, Icons.Default.FormatBold, "Negrito"),
    ToolbarItem(ToolbarItemType.Italic, Icons.Default.FormatItalic, "Itálico"),
    ToolbarItem(ToolbarItemType.Underline, Icons.Default.FormatUnderlined, "Sublinhado"),
    ToolbarItem(ToolbarItemType.AlignLeft, Icons.Default.FormatAlignCenter, "Alinhar à esquerda"), // Usando center como fallback
    ToolbarItem(ToolbarItemType.AlignCenter, Icons.Default.FormatAlignCenter, "Centralizar"),
    ToolbarItem(ToolbarItemType.AlignRight, Icons.Default.FormatAlignCenter, "Alinhar à direita"), // Usando center como fallback
    ToolbarItem(ToolbarItemType.OrderedList, Icons.Default.FormatListNumbered, "Lista numerada"),
    ToolbarItem(ToolbarItemType.UnorderedList, Icons.Default.FormatListNumbered, "Lista com marcadores"), // Usando numbered como fallback
    ToolbarItem(ToolbarItemType.Code, Icons.Default.Code, "Código"),
    ToolbarItem(ToolbarItemType.Link, Icons.Default.Link, "Link")
)
