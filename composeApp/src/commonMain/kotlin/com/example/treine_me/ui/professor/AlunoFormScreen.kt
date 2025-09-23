package com.example.treine_me.ui.professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treine_me.api.AlunoCreateRequest
import com.example.treine_me.services.AlunoService
import com.example.treine_me.services.AlunoUpdateRequest
import com.example.treine_me.services.PlanoService
import com.example.treine_me.api.PlanoResponse
import com.example.treine_me.services.AlunoComInscricoesResponse
import com.example.treine_me.ui.professor.components.*
import com.example.treine_me.ui.util.ShowToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlunoFormScreen(
    alunoId: String? = null, // null = modo criação, não null = modo edição
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var fotoPerfilUrl by remember { mutableStateOf("") }
    var selectedPlanoId by remember { mutableStateOf<String?>(null) }
    
    var planos by remember { mutableStateOf<List<PlanoResponse>>(emptyList()) }
    var isLoadingPlanos by remember { mutableStateOf(true) }
    var isLoadingAluno by remember { mutableStateOf(alunoId != null) }
    var isSaving by remember { mutableStateOf(false) }
    
    var nomeError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var senhaError by remember { mutableStateOf<String?>(null) }
    var planoError by remember { mutableStateOf<String?>(null) }
    
    var pendingToast by remember { mutableStateOf<String?>(null) }

    val alunoService = remember { AlunoService() }
    val planoService = remember { PlanoService() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val isEditMode = alunoId != null
    val canSave = nome.isNotBlank() && email.isNotBlank() && 
                  (!isEditMode && senha.isNotBlank() || isEditMode) &&
                  (!isEditMode && selectedPlanoId != null || isEditMode) &&
                  !isSaving

    // Carregar planos
    LaunchedEffect(Unit) {
        scope.launch {
            val response = planoService.listPlanos()
            if (response.success) {
                response.data?.let { data ->
                    planos = data.data
                }
            }
            isLoadingPlanos = false
        }
    }

    // Carregar aluno para edição
    LaunchedEffect(alunoId) {
        if (alunoId != null) {
            scope.launch {
            val response = alunoService.getAluno(alunoId)
            if (response.success) {
                response.data?.let { aluno ->
                    nome = aluno.nome
                    email = aluno.email
                    fotoPerfilUrl = aluno.fotoPerfilUrl ?: ""
                }
            } else {
                pendingToast = response.error?.message ?: "Erro ao carregar aluno"
            }
                isLoadingAluno = false
            }
        }
    }

    // Validação
    fun validateForm(): Boolean {
        var isValid = true
        
        nomeError = if (nome.isBlank()) {
            isValid = false
            "Nome é obrigatório"
        } else null

        emailError = if (email.isBlank()) {
            isValid = false
            "Email é obrigatório"
        } else if (!email.contains("@")) {
            isValid = false
            "Email deve ter formato válido"
        } else null

        if (!isEditMode) {
            senhaError = if (senha.length < 6) {
                isValid = false
                "Senha deve ter pelo menos 6 caracteres"
            } else null

            planoError = if (selectedPlanoId == null) {
                isValid = false
                "Selecione um plano"
            } else null
        }

        return isValid
    }

    // Salvar aluno
    fun saveAluno() {
        if (!validateForm()) return

        scope.launch {
            isSaving = true
            
            val response = if (isEditMode) {
                alunoService.updateAluno(
                    alunoId!!,
                    AlunoUpdateRequest(
                        nome = nome.takeIf { it.isNotBlank() },
                        fotoPerfilUrl = fotoPerfilUrl.takeIf { it.isNotBlank() }
                    )
                )
            } else {
                alunoService.createAluno(
                    AlunoCreateRequest(
                        nome = nome,
                        email = email,
                        senha = senha,
                        fotoPerfilUrl = fotoPerfilUrl.takeIf { it.isNotBlank() },
                        planoId = selectedPlanoId!!
                    )
                )
            }

            if (response.success) {
                pendingToast = if (isEditMode) "Aluno atualizado com sucesso" else "Aluno criado com sucesso"
                onSaved()
            } else {
                pendingToast = response.error?.message ?: "Erro ao salvar aluno"
            }
            
            isSaving = false
        }
    }

    // Toast
    pendingToast?.let { message ->
        ShowToast(message)
        pendingToast = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (isEditMode) "Editar Aluno" else "Novo Aluno",
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
                },
                actions = {
                    TextButton(
                        onClick = ::saveAluno,
                        enabled = canSave
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salvar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoadingAluno) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
                // Pré-visualização
                AlunoFormPreview(
                    nome = nome,
                    email = email,
                    fotoPerfilUrl = fotoPerfilUrl
                )

                // Campos do formulário
                AlunoFormFields(
                    nome = nome,
                    onNomeChange = { 
                        nome = it
                        nomeError = null
                    },
                    email = email,
                    onEmailChange = { 
                        email = it
                        emailError = null
                    },
                    senha = senha,
                    onSenhaChange = { 
                        senha = it
                        senhaError = null
                    },
                    fotoPerfilUrl = fotoPerfilUrl,
                    onFotoPerfilUrlChange = { fotoPerfilUrl = it },
                    isEditMode = isEditMode,
                    nomeError = nomeError,
                    emailError = emailError,
                    senhaError = senhaError
                )

                // Seletor de plano (apenas no modo criação)
                if (!isEditMode) {
                    PlanoSelector(
                        planos = planos,
                        selectedPlanoId = selectedPlanoId,
                        onPlanoSelected = { 
                            selectedPlanoId = it
                            planoError = null
                        },
                        isLoading = isLoadingPlanos,
                        error = planoError
                    )
                }

                // Botão de salvar (versão grande no final)
                Button(
                    onClick = ::saveAluno,
                    enabled = canSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditMode) "Salvar Alterações" else "Criar Aluno",
                            fontSize = 16.sp
                        )
                    }
                }

                // Espaço extra no final
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
