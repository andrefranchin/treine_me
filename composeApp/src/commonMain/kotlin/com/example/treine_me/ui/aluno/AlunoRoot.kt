package com.example.treine_me.ui.aluno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treine_me.ui.aluno.components.ProdutoCard
import com.example.treine_me.ui.aluno.components.ModuloCard
import com.example.treine_me.api.ProdutoResponse
import com.example.treine_me.api.ModuloResponse
import com.example.treine_me.api.PublicAulaResponse
import com.example.treine_me.api.TipoProduto
import com.example.treine_me.services.PublicService
import kotlinx.coroutines.launch

@Composable
fun AlunoRoot() {
    var currentRoute by remember { mutableStateOf<AlunoRoute>(AlunoRoute.Welcome) }
    
    when (currentRoute) {
        is AlunoRoute.Welcome -> {
            WelcomeScreen(
                onGetStarted = {
                    currentRoute = AlunoRoute.ExploreClasses
                }
            )
        }
        
        is AlunoRoute.ExploreClasses -> {
            ExploreClassesScreen(
                onNavigateToTraining = { produtoId ->
                    currentRoute = AlunoRoute.Training(produtoId)
                },
                onBack = {
                    currentRoute = AlunoRoute.Welcome
                }
            )
        }
        
        is AlunoRoute.Training -> {
            val trainingRoute = currentRoute as AlunoRoute.Training
            TrainingScreen(
                produtoId = trainingRoute.produtoId,
                onBack = {
                    currentRoute = AlunoRoute.ExploreClasses
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreClassesScreen(
    onNavigateToTraining: (String) -> Unit,
    onBack: () -> Unit
) {
    val publicService = remember { PublicService() }
    val scope = rememberCoroutineScope()
    
    var produtos by remember { mutableStateOf<List<ProdutoResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // ID do professor fixo para este app
    val professorId = "ba273d71-9f1b-4c1e-b732-dff3913750e1"
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                error = null
                val response = publicService.listProdutos(professorId)
                if (response.success) {
                    produtos = response.data?.data ?: emptyList()
                } else {
                    error = response.error?.message ?: "Erro ao carregar produtos"
                }
            } catch (e: Exception) {
                error = e.message ?: "Erro desconhecido"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header com botão voltar
        TopAppBar(
            title = {
                Text(
                    text = "Explorar Aulas",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6366F1),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
        
        // Conteúdo principal
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF6366F1)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header com descrição
                item {
                    Column {
                        Text(
                            text = "Descubra nossos cursos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Escolha entre nossos programas de treino e comece sua jornada fitness",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                        
                        // Mostrar erro se houver
                        error?.let { errorMessage ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                fontSize = 12.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                // Lista de produtos
                if (produtos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum curso disponível no momento",
                                fontSize = 16.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    items(produtos) { produto ->
                        ProdutoCard(
                            produto = produto,
                            onClick = {
                                onNavigateToTraining(produto.id)
                            }
                        )
                    }
                }
                
                // Espaçamento no final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingScreen(
    produtoId: String,
    onBack: () -> Unit
) {
    val publicService = remember { PublicService() }
    val scope = rememberCoroutineScope()
    
    var produto by remember { mutableStateOf<ProdutoResponse?>(null) }
    var modulos by remember { mutableStateOf<List<ModuloResponse>>(emptyList()) }
    var aulasMap by remember { mutableStateOf<Map<String, List<PublicAulaResponse>>>(emptyMap()) }
    var expandedModulos by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val professorId = "ba273d71-9f1b-4c1e-b732-dff3913750e1"
    
    LaunchedEffect(produtoId) {
        scope.launch {
            try {
                isLoading = true
                error = null
                
                // Carregar produto
                val produtoResponse = publicService.getProduto(produtoId, professorId)
                if (produtoResponse.success) {
                    produto = produtoResponse.data
                } else {
                    error = produtoResponse.error?.message ?: "Erro ao carregar produto"
                    return@launch
                }
                
                // Carregar módulos
                val modulosResponse = publicService.listModulosByProduto(produtoId, professorId)
                if (modulosResponse.success) {
                    modulos = modulosResponse.data ?: emptyList()
                    
                    // Carregar aulas para cada módulo
                    val aulasTemp = mutableMapOf<String, List<PublicAulaResponse>>()
                    modulos.forEach { modulo ->
                        val aulasResponse = publicService.listAulasByModulo(modulo.id, professorId)
                        if (aulasResponse.success) {
                            aulasTemp[modulo.id] = aulasResponse.data ?: emptyList()
                        }
                    }
                    aulasMap = aulasTemp
                } else {
                    error = modulosResponse.error?.message ?: "Erro ao carregar módulos"
                }
            } catch (e: Exception) {
                error = e.message ?: "Erro desconhecido"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = produto?.titulo ?: "Carregando...",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6366F1),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF6366F1)
                )
            }
        } else if (error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Erro ao carregar conteúdo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Voltar")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Descrição do produto
                item {
                    produto?.let { prod ->
                        Column {
                            Text(
                                text = prod.descricao,
                                fontSize = 16.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Módulos do Curso",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                
                // Lista de módulos
                if (modulos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum módulo disponível",
                                fontSize = 16.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    items(modulos) { modulo ->
                        val aulas = aulasMap[modulo.id] ?: emptyList()
                        val isExpanded = expandedModulos.contains(modulo.id)
                        
                        ModuloCard(
                            modulo = modulo,
                            aulas = aulas,
                            isExpanded = isExpanded,
                            onExpandToggle = {
                                expandedModulos = if (isExpanded) {
                                    expandedModulos - modulo.id
                                } else {
                                    expandedModulos + modulo.id
                                }
                            },
                            onAulaClick = { aula ->
                                // TODO: Implementar navegação para aula específica
                                println("Aula clicada: ${aula.titulo}")
                            }
                        )
                    }
                }
                
                // Espaçamento no final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}