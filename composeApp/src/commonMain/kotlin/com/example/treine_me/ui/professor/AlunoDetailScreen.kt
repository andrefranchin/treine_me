package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
fun AlunoDetailScreen(
    alunoId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    var aluno by remember { mutableStateOf<AlunoComInscricoesResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var isResettingPassword by remember { mutableStateOf(false) }
    var pendingToast by remember { mutableStateOf<String?>(null) }

    val alunoService = remember { AlunoService() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Carregar dados do aluno
    LaunchedEffect(alunoId) {
        scope.launch {
            isLoading = true
            val response = alunoService.getAluno(alunoId)
            if (response.success && response.data != null) {
                aluno = response.data
            } else {
                pendingToast = response.error?.message ?: "Erro ao carregar aluno"
            }
            isLoading = false
        }
    }

    // Função para recarregar dados
    fun reloadAluno() {
        scope.launch {
            val response = alunoService.getAluno(alunoId)
            if (response.success && response.data != null) {
                aluno = response.data
            }
        }
    }

    // Função para desativar aluno
    fun deactivateAluno() {
        aluno?.let { currentAluno ->
            scope.launch {
                val response = alunoService.deactivateAluno(currentAluno.id)
                if (response.success) {
                    pendingToast = "Aluno ${currentAluno.nome} desativado com sucesso"
                    onBack() // Volta para a lista
                } else {
                    pendingToast = response.error?.message ?: "Erro ao desativar aluno"
                }
                showDeactivateDialog = false
            }
        }
    }

    // Função para resetar senha
    fun resetPassword() {
        aluno?.let { currentAluno ->
            scope.launch {
                isResettingPassword = true
                val response = alunoService.resetAlunoPassword(
                    currentAluno.id,
                    ResetAlunoPasswordRequest(newPassword)
                )
                if (response.success) {
                    pendingToast = "Senha resetada com sucesso para ${currentAluno.nome}"
                    showResetPasswordDialog = false
                    newPassword = ""
                } else {
                    pendingToast = response.error?.message ?: "Erro ao resetar senha"
                }
                isResettingPassword = false
            }
        }
    }

    // Toast
    pendingToast?.let { message ->
        ShowToast(message)
        pendingToast = null
    }

    // Diálogos
    if (showDeactivateDialog && aluno != null) {
        DeactivateAlunoDialog(
            alunoNome = aluno!!.nome,
            onConfirm = ::deactivateAluno,
            onDismiss = { showDeactivateDialog = false }
        )
    }

    if (showResetPasswordDialog && aluno != null) {
        ResetPasswordDialog(
            alunoNome = aluno!!.nome,
            newPassword = newPassword,
            onNewPasswordChange = { newPassword = it },
            onConfirm = ::resetPassword,
            onDismiss = { 
                showResetPasswordDialog = false
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
                        text = aluno?.nome ?: "Detalhes do Aluno",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (aluno == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aluno não encontrado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onBack) {
                        Text("Voltar à lista")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header com informações principais
                AlunoDetailHeader(
                    aluno = aluno!!,
                    onEdit = onEdit
                )

                // Inscrições ativas
                if (aluno!!.inscricoesAtivas.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Planos Ativos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            aluno!!.inscricoesAtivas.forEachIndexed { index, inscricao ->
                                InscricaoCard(inscricao = inscricao)
                                if (index < aluno!!.inscricoesAtivas.size - 1) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Nenhum plano ativo",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Este aluno não possui planos ativos no momento",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Ações
                AlunoActionsCard(
                    onEdit = onEdit,
                    onResetPassword = { 
                        showResetPasswordDialog = true
                        newPassword = ""
                    },
                    onDeactivate = { showDeactivateDialog = true }
                )

                // Espaço extra no final
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
