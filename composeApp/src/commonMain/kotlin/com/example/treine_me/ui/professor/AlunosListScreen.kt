package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treine_me.services.AlunoService
import com.example.treine_me.services.AlunoComInscricoesResponse
import com.example.treine_me.services.ResetAlunoPasswordRequest
import com.example.treine_me.ui.professor.components.*
import com.example.treine_me.ui.util.ShowToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlunosListScreen(
    onAddAluno: () -> Unit = {},
    onEditAluno: (String) -> Unit = {},
    onAlunoDetail: (String) -> Unit = {}
) {
    var alunos by remember { mutableStateOf<List<AlunoComInscricoesResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeactivateDialog by remember { mutableStateOf<AlunoComInscricoesResponse?>(null) }
    var showResetPasswordDialog by remember { mutableStateOf<AlunoComInscricoesResponse?>(null) }
    var newPassword by remember { mutableStateOf("") }
    var isResettingPassword by remember { mutableStateOf(false) }
    var pendingToast by remember { mutableStateOf<String?>(null) }

    val alunoService = remember { AlunoService() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Filtrar alunos baseado na busca
    val filteredAlunos = remember(alunos, searchQuery) {
        if (searchQuery.isBlank()) {
            alunos
        } else {
            alunos.filter { aluno ->
                aluno.nome.contains(searchQuery, ignoreCase = true) ||
                aluno.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Carregar alunos
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val response = alunoService.listAlunos()
            if (response.success) {
                response.data?.let { data ->
                    alunos = data.data
                }
            } else {
                pendingToast = response.error?.message ?: "Erro ao carregar alunos"
            }
            isLoading = false
        }
    }

    // Função para recarregar
    fun reloadAlunos() {
        scope.launch {
            isLoading = true
            val response = alunoService.listAlunos()
            if (response.success) {
                response.data?.let { data ->
                    alunos = data.data
                }
            } else {
                pendingToast = response.error?.message ?: "Erro ao carregar alunos"
            }
            isLoading = false
        }
    }

    // Função para desativar aluno
    fun deactivateAluno(aluno: AlunoComInscricoesResponse) {
        scope.launch {
            val response = alunoService.deactivateAluno(aluno.id)
            if (response.success) {
                pendingToast = "Aluno ${aluno.nome} desativado com sucesso"
                reloadAlunos()
            } else {
                pendingToast = response.error?.message ?: "Erro ao desativar aluno"
            }
            showDeactivateDialog = null
        }
    }

    // Função para resetar senha
    fun resetPassword(aluno: AlunoComInscricoesResponse) {
        scope.launch {
            isResettingPassword = true
            val response = alunoService.resetAlunoPassword(
                aluno.id,
                ResetAlunoPasswordRequest(newPassword)
            )
            if (response.success) {
                pendingToast = "Senha resetada com sucesso para ${aluno.nome}"
                showResetPasswordDialog = null
                newPassword = ""
            } else {
                pendingToast = response.error?.message ?: "Erro ao resetar senha"
            }
            isResettingPassword = false
        }
    }

    // Toast
    pendingToast?.let { message ->
        ShowToast(message)
        pendingToast = null
    }

    // Diálogos
    showDeactivateDialog?.let { aluno ->
        DeactivateAlunoDialog(
            alunoNome = aluno.nome,
            onConfirm = { deactivateAluno(aluno) },
            onDismiss = { showDeactivateDialog = null }
        )
    }

    showResetPasswordDialog?.let { aluno ->
        ResetPasswordDialog(
            alunoNome = aluno.nome,
            newPassword = newPassword,
            onNewPasswordChange = { newPassword = it },
            onConfirm = { resetPassword(aluno) },
            onDismiss = { 
                showResetPasswordDialog = null
                newPassword = ""
            },
            isLoading = isResettingPassword
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Alunos",
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = onAddAluno) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar aluno"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAluno,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar aluno"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Barra de busca
            AlunosSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onClearSearch = { searchQuery = "" },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredAlunos.isEmpty() && searchQuery.isBlank()) {
                AlunosEmptyState(
                    onAddAluno = onAddAluno
                )
            } else if (filteredAlunos.isEmpty() && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum aluno encontrado para \"$searchQuery\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredAlunos,
                        key = { it.id }
                    ) { aluno ->
                        AlunoListItem(
                            aluno = aluno,
                            onEdit = { onEditAluno(aluno.id) },
                            onDetail = { onAlunoDetail(aluno.id) },
                            onResetPassword = { 
                                showResetPasswordDialog = aluno
                                newPassword = ""
                            },
                            onDeactivate = { showDeactivateDialog = aluno }
                        )
                    }
                }
            }
        }
    }
}
