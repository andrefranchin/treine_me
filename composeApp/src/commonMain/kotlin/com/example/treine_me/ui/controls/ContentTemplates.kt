package com.example.treine_me.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Template de conteúdo para estruturar aulas
 */
data class ContentTemplate(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val htmlContent: String
)

/**
 * Componente de seleção de templates de conteúdo
 */
@Composable
fun ContentTemplateSelector(
    selectedTemplate: ContentTemplate? = null,
    onTemplateSelected: (ContentTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTemplateDialog by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(
            text = "Template de Conteúdo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTemplateDialog = true },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedTemplate != null) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = selectedTemplate?.icon ?: Icons.Default.Article,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (selectedTemplate != null) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedTemplate?.name ?: "Selecionar Template",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selectedTemplate != null) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTemplate != null) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (selectedTemplate != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = selectedTemplate.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expandir",
                    tint = if (selectedTemplate != null) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    // Dialog de seleção de templates
    if (showTemplateDialog) {
        TemplateSelectionDialog(
            currentTemplate = selectedTemplate,
            onTemplateSelected = { template ->
                onTemplateSelected(template)
                showTemplateDialog = false
            },
            onDismiss = { showTemplateDialog = false }
        )
    }
}

@Composable
private fun TemplateSelectionDialog(
    currentTemplate: ContentTemplate?,
    onTemplateSelected: (ContentTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar Template") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(getAvailableTemplates()) { template ->
                    TemplateItem(
                        template = template,
                        isSelected = currentTemplate?.id == template.id,
                        onClick = { onTemplateSelected(template) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun TemplateItem(
    template: ContentTemplate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) 
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
        else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = template.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(Modifier.height(2.dp))
                
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selecionado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Templates pré-definidos para diferentes tipos de aula
 */
private fun getAvailableTemplates(): List<ContentTemplate> = listOf(
    ContentTemplate(
        id = "basic",
        name = "Básico",
        description = "Estrutura simples com introdução, conteúdo e conclusão",
        icon = Icons.Default.Article,
        htmlContent = """
            <h2>Introdução</h2>
            <p>Apresente o tópico da aula e os objetivos de aprendizagem.</p>
            
            <h2>Conteúdo Principal</h2>
            <p>Desenvolva o conteúdo principal da aula aqui.</p>
            
            <h2>Conclusão</h2>
            <p>Resuma os pontos principais e próximos passos.</p>
        """.trimIndent()
    ),
    ContentTemplate(
        id = "tutorial",
        name = "Tutorial Passo a Passo",
        description = "Ideal para aulas práticas com instruções detalhadas",
        icon = Icons.Default.List,
        htmlContent = """
            <h2>🎯 Objetivo</h2>
            <p>Descreva o que o aluno aprenderá nesta aula.</p>
            
            <h2>📋 Pré-requisitos</h2>
            <ul>
                <li>Item necessário 1</li>
                <li>Item necessário 2</li>
            </ul>
            
            <h2>📝 Passo a Passo</h2>
            <h3>Passo 1: Título do primeiro passo</h3>
            <p>Descrição detalhada do primeiro passo.</p>
            
            <h3>Passo 2: Título do segundo passo</h3>
            <p>Descrição detalhada do segundo passo.</p>
            
            <h2>✅ Verificação</h2>
            <p>Como verificar se o resultado está correto.</p>
        """.trimIndent()
    ),
    ContentTemplate(
        id = "theory",
        name = "Aula Teórica",
        description = "Para conceitos teóricos com definições e exemplos",
        icon = Icons.Default.School,
        htmlContent = """
            <h2>📚 Conceitos Fundamentais</h2>
            <p>Introduza os conceitos principais que serão abordados.</p>
            
            <h2>🔍 Definições</h2>
            <p><strong>Termo 1:</strong> Definição clara e objetiva.</p>
            <p><strong>Termo 2:</strong> Definição clara e objetiva.</p>
            
            <h2>💡 Exemplos Práticos</h2>
            <p>Apresente exemplos que ilustrem os conceitos.</p>
            
            <h2>🧠 Pontos Importantes</h2>
            <ul>
                <li>Ponto importante 1</li>
                <li>Ponto importante 2</li>
            </ul>
        """.trimIndent()
    ),
    ContentTemplate(
        id = "exercise",
        name = "Exercício Prático",
        description = "Para atividades práticas e exercícios",
        icon = Icons.Default.Assignment,
        htmlContent = """
            <h2>🎯 Objetivo do Exercício</h2>
            <p>Descreva o que será praticado neste exercício.</p>
            
            <h2>📋 Instruções</h2>
            <ol>
                <li>Primeira instrução</li>
                <li>Segunda instrução</li>
                <li>Terceira instrução</li>
            </ol>
            
            <h2>💻 Código/Recursos</h2>
            <code>
            // Exemplo de código ou recursos necessários
            </code>
            
            <h2>🎉 Resultado Esperado</h2>
            <p>Descreva o resultado que o aluno deve obter.</p>
        """.trimIndent()
    ),
    ContentTemplate(
        id = "review",
        name = "Revisão e Resumo",
        description = "Para consolidar conhecimentos e fazer revisão",
        icon = Icons.Default.Refresh,
        htmlContent = """
            <h2>🔄 Revisão da Aula</h2>
            <p>Recapitule os principais tópicos abordados.</p>
            
            <h2>📝 Pontos-Chave</h2>
            <ul>
                <li>Ponto-chave 1</li>
                <li>Ponto-chave 2</li>
                <li>Ponto-chave 3</li>
            </ul>
            
            <h2>❓ Perguntas de Reflexão</h2>
            <ol>
                <li>Pergunta reflexiva 1?</li>
                <li>Pergunta reflexiva 2?</li>
            </ol>
            
            <h2>🚀 Próximos Passos</h2>
            <p>O que estudar ou praticar a seguir.</p>
        """.trimIndent()
    ),
    ContentTemplate(
        id = "case_study",
        name = "Estudo de Caso",
        description = "Para análise de casos reais e situações práticas",
        icon = Icons.Default.Analytics,
        htmlContent = """
            <h2>📖 Contexto</h2>
            <p>Apresente o cenário ou situação que será analisada.</p>
            
            <h2>🎯 Problema</h2>
            <p>Descreva o problema ou desafio a ser resolvido.</p>
            
            <h2>🔍 Análise</h2>
            <p>Analise os fatores envolvidos e possíveis soluções.</p>
            
            <h2>💡 Solução Proposta</h2>
            <p>Apresente a solução e justifique a escolha.</p>
            
            <h2>📊 Resultados</h2>
            <p>Mostre os resultados obtidos com a solução.</p>
        """.trimIndent()
    )
)
