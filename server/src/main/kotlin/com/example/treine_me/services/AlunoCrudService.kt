package com.example.treine_me.services

import com.example.treine_me.auth.PasswordHelper
import com.example.treine_me.exceptions.ConflictException
import com.example.treine_me.exceptions.ForbiddenException
import com.example.treine_me.exceptions.NotFoundException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.*
import com.example.treine_me.enums.StatusInscricao
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Serviço CRUD completo para alunos.
 * Permite que professores gerenciem seus alunos.
 */
class AlunoCrudService {
    
    // ========== CRUD DE ALUNOS ==========
    
    fun createAluno(request: AlunoCreateRequest, professorId: String): AlunoResponse {
        validateAlunoRequest(request)
        
        return transaction {
            // Verificar se professor existe e está ativo
            val professor = ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
            
            // Verificar se plano existe e pertence ao professor
            val plano = PlanoEntity.find {
                (Planos.id eq UUID.fromString(request.planoId)) and
                (Planos.professorId eq professor.id) and
                (Planos.isActive eq true)
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado ou não pertence ao professor")
            
            // Verificar se email já existe
            val existingAluno = AlunoEntity.find { Alunos.email eq request.email }.firstOrNull()
            val existingProfessor = ProfessorEntity.find { Professores.email eq request.email }.firstOrNull()
            
            if (existingAluno != null || existingProfessor != null) {
                throw ConflictException("Email já está em uso")
            }
            
            val now = Clock.System.now()
            
            // Criar o aluno
            val aluno = AlunoEntity.new {
                nome = request.nome
                email = request.email
                senhaHash = PasswordHelper.hashPassword(request.senha)
                fotoPerfilUrl = request.fotoPerfilUrl
                dtIns = now
                dtUpd = now
                idUserIns = UUID.fromString(professorId)
                idUserUpd = UUID.fromString(professorId)
                isActive = true
            }
            
            // Criar inscrição automática no plano selecionado
            InscricaoEntity.new {
                this.aluno = aluno
                this.plano = plano
                dtInicio = now
                dtFim = null // Inscrição sem data de fim (permanente até ser cancelada)
                status = StatusInscricao.ATIVA
                dtIns = now
                dtUpd = now
                idUserIns = UUID.fromString(professorId)
                idUserUpd = UUID.fromString(professorId)
                isActive = true
            }
            
            AlunoResponse(
                id = aluno.id.value.toString(),
                nome = aluno.nome,
                email = aluno.email,
                fotoPerfilUrl = aluno.fotoPerfilUrl
            )
        }
    }
    
    fun listAlunosByProfessor(professorId: String, page: Int = 1, size: Int = 20): PaginatedResponse<AlunoComInscricoesResponse> {
        return transaction {
            // Verificar se professor existe
            ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
            
            val offset = (page - 1) * size
            
            // Buscar alunos que têm inscrições com planos do professor
            val alunosComInscricoes = InscricaoEntity.find {
                (Inscricoes.isActive eq true)
            }.filter { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }.map { it.aluno }
                .distinctBy { it.id.value }
                .drop(offset)
                .take(size)
                .map { aluno ->
                    // Buscar inscrições ativas do aluno com este professor
                    val inscricoesAtivas = InscricaoEntity.find {
                        (Inscricoes.alunoId eq aluno.id) and
                        (Inscricoes.status eq StatusInscricao.ATIVA) and
                        (Inscricoes.isActive eq true)
                    }.filter { inscricao ->
                        inscricao.plano.professor.id.value.toString() == professorId
                    }.map { inscricao ->
                        InscricaoResponse(
                            id = inscricao.id.value.toString(),
                            dtInicio = inscricao.dtInicio,
                            dtFim = inscricao.dtFim,
                            status = inscricao.status,
                            aluno = AlunoResponse(
                                id = aluno.id.value.toString(),
                                nome = aluno.nome,
                                email = aluno.email,
                                fotoPerfilUrl = aluno.fotoPerfilUrl
                            ),
                            plano = PlanoResponse(
                                id = inscricao.plano.id.value.toString(),
                                nome = inscricao.plano.nome,
                                descricao = inscricao.plano.descricao,
                                valor = inscricao.plano.valor.toString(),
                                recorrencia = inscricao.plano.recorrencia,
                                professorId = inscricao.plano.professor.id.value.toString(),
                                professorNome = inscricao.plano.professor.nome
                            )
                        )
                    }
                    
                    AlunoComInscricoesResponse(
                        id = aluno.id.value.toString(),
                        nome = aluno.nome,
                        email = aluno.email,
                        fotoPerfilUrl = aluno.fotoPerfilUrl,
                        inscricoesAtivas = inscricoesAtivas
                    )
                }
            
            // Contar total de alunos únicos com inscrições deste professor
            val totalAlunos = InscricaoEntity.find {
                (Inscricoes.isActive eq true)
            }.filter { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }.map { it.aluno.id.value }
                .distinct()
                .count()
            
            PaginatedResponse(
                data = alunosComInscricoes,
                page = page,
                size = size,
                total = totalAlunos.toLong(),
                totalPages = ((totalAlunos + size - 1) / size).toLong()
            )
        }
    }
    
    fun getAluno(alunoId: String, professorId: String): AlunoComInscricoesResponse {
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            // Verificar se o aluno tem inscrições com planos deste professor
            val inscricoesComProfessor = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and (Inscricoes.isActive eq true)
            }.filter { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
            
            if (inscricoesComProfessor.isEmpty()) {
                throw ForbiddenException("Você não tem permissão para acessar este aluno")
            }
            
            val inscricoesAtivas = inscricoesComProfessor
                .filter { it.status == StatusInscricao.ATIVA }
                .map { inscricao ->
                    InscricaoResponse(
                        id = inscricao.id.value.toString(),
                        dtInicio = inscricao.dtInicio,
                        dtFim = inscricao.dtFim,
                        status = inscricao.status,
                        aluno = AlunoResponse(
                            id = aluno.id.value.toString(),
                            nome = aluno.nome,
                            email = aluno.email,
                            fotoPerfilUrl = aluno.fotoPerfilUrl
                        ),
                        plano = PlanoResponse(
                            id = inscricao.plano.id.value.toString(),
                            nome = inscricao.plano.nome,
                            descricao = inscricao.plano.descricao,
                            valor = inscricao.plano.valor.toString(),
                            recorrencia = inscricao.plano.recorrencia,
                            professorId = inscricao.plano.professor.id.value.toString(),
                            professorNome = inscricao.plano.professor.nome
                        )
                    )
                }
            
            AlunoComInscricoesResponse(
                id = aluno.id.value.toString(),
                nome = aluno.nome,
                email = aluno.email,
                fotoPerfilUrl = aluno.fotoPerfilUrl,
                inscricoesAtivas = inscricoesAtivas
            )
        }
    }
    
    fun updateAluno(alunoId: String, request: AlunoUpdateRequest, professorId: String): AlunoResponse {
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            // Verificar se o professor tem permissão para editar este aluno
            val temPermissao = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and (Inscricoes.isActive eq true)
            }.any { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
            
            if (!temPermissao) {
                throw ForbiddenException("Você não tem permissão para editar este aluno")
            }
            
            val now = Clock.System.now()
            
            request.nome?.let { aluno.nome = it }
            request.fotoPerfilUrl?.let { aluno.fotoPerfilUrl = it }
            
            aluno.dtUpd = now
            aluno.idUserUpd = UUID.fromString(professorId)
            
            AlunoResponse(
                id = aluno.id.value.toString(),
                nome = aluno.nome,
                email = aluno.email,
                fotoPerfilUrl = aluno.fotoPerfilUrl
            )
        }
    }
    
    fun deactivateAluno(alunoId: String, professorId: String): Boolean {
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            // Verificar se o professor tem permissão
            val temPermissao = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and (Inscricoes.isActive eq true)
            }.any { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
            
            if (!temPermissao) {
                throw ForbiddenException("Você não tem permissão para desativar este aluno")
            }
            
            val now = Clock.System.now()
            aluno.isActive = false
            aluno.dtUpd = now
            aluno.idUserUpd = UUID.fromString(professorId)
            
            // Cancelar todas as inscrições ativas do aluno com planos deste professor
            InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and 
                (Inscricoes.status eq StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true)
            }.filter { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }.forEach { inscricao ->
                inscricao.status = StatusInscricao.CANCELADA
                inscricao.dtUpd = now
                inscricao.idUserUpd = UUID.fromString(professorId)
            }
            
            true
        }
    }
    
    fun resetAlunoPassword(alunoId: String, newPassword: String, professorId: String): Boolean {
        if (newPassword.length < 6) {
            throw ValidationException("Senha deve ter pelo menos 6 caracteres", "senha")
        }
        
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            // Verificar permissão
            val temPermissao = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and (Inscricoes.isActive eq true)
            }.any { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
            
            if (!temPermissao) {
                throw ForbiddenException("Você não tem permissão para resetar a senha deste aluno")
            }
            
            val now = Clock.System.now()
            aluno.senhaHash = PasswordHelper.hashPassword(newPassword)
            aluno.dtUpd = now
            aluno.idUserUpd = UUID.fromString(professorId)
            
            true
        }
    }
    
    // ========== UTILITÁRIOS ==========
    
    private fun validateAlunoRequest(request: AlunoCreateRequest) {
        if (request.nome.isBlank()) {
            throw ValidationException("Nome é obrigatório", "nome")
        }
        if (request.email.isBlank()) {
            throw ValidationException("Email é obrigatório", "email")
        }
        if (!request.email.contains("@")) {
            throw ValidationException("Email deve ter formato válido", "email")
        }
        if (request.senha.length < 6) {
            throw ValidationException("Senha deve ter pelo menos 6 caracteres", "senha")
        }
        if (request.planoId.isBlank()) {
            throw ValidationException("ID do plano é obrigatório", "planoId")
        }
    }
}

@kotlinx.serialization.Serializable
data class AlunoComInscricoesResponse(
    val id: String,
    val nome: String,
    val email: String,
    val fotoPerfilUrl: String? = null,
    val inscricoesAtivas: List<InscricaoResponse>
)

@kotlinx.serialization.Serializable
data class ResetAlunoPasswordRequest(
    val newPassword: String
)
