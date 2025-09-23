package com.example.treine_me.services

import com.example.treine_me.auth.JwtConfig
import com.example.treine_me.auth.PasswordHelper
import com.example.treine_me.dto.LoginRequest
import com.example.treine_me.dto.LoginResponse
import com.example.treine_me.dto.UserInfo
import com.example.treine_me.enums.UserRole
import com.example.treine_me.enums.StatusInscricao
import com.example.treine_me.exceptions.AuthenticationException
import com.example.treine_me.exceptions.ForbiddenException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Serviço de autenticação específico para alunos.
 * Permite login de aluno com controle de acesso por professor.
 */
class AlunoAuthService {
    
    /**
     * Login de aluno com validação de professor.
     * O aluno deve ter pelo menos uma inscrição ativa com o professor especificado.
     */
    fun loginAluno(request: AlunoLoginRequest): LoginResponse {
        validateAlunoLoginRequest(request)
        
        return transaction {
            // Buscar aluno
            val aluno = AlunoEntity.find { 
                (Alunos.email eq request.email) and (Alunos.isActive eq true) 
            }.firstOrNull()
            
            if (aluno == null) {
                throw AuthenticationException("Aluno não encontrado com este email")
            }
            
            if (!PasswordHelper.verifyPassword(request.senha, aluno.senhaHash)) {
                throw AuthenticationException("Senha incorreta")
            }
            
            // Validar se o professor existe e está ativo
            ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(request.professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw AuthenticationException("Professor não encontrado")
            
            // Verificar se o aluno tem inscrição ativa com este professor
            val inscricaoAtiva = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and
                (Inscricoes.status eq StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true)
            }.firstOrNull { inscricao ->
                inscricao.plano.professor.id.value.toString() == request.professorId
            }
            
            if (inscricaoAtiva == null) {
                throw ForbiddenException("Você não possui acesso ativo aos conteúdos deste professor")
            }
            
            // Gerar token com informações do professor no payload
            val token = JwtConfig.generateToken(
                userId = aluno.id.value.toString(),
                role = UserRole.ALUNO,
                email = aluno.email
            )
            
            LoginResponse(
                token = token,
                user = UserInfo(
                    id = aluno.id.value.toString(),
                    nome = aluno.nome,
                    email = aluno.email,
                    role = UserRole.ALUNO,
                    fotoPerfilUrl = aluno.fotoPerfilUrl
                )
            )
        }
    }
    
    
    /**
     * Gera token para um aluno específico com um professor específico.
     * Usado após o aluno escolher qual professor acessar.
     */
    fun gerarTokenProfessor(alunoId: String, professorId: String): LoginResponse {
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw AuthenticationException("Aluno não encontrado")
            
            ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw AuthenticationException("Professor não encontrado")
            
            // Verificar se o aluno tem inscrição ativa com este professor
            val inscricaoAtiva = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and
                (Inscricoes.status eq StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true)
            }.firstOrNull { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
            
            if (inscricaoAtiva == null) {
                throw ForbiddenException("Você não possui acesso ativo aos conteúdos deste professor")
            }
            
            val token = JwtConfig.generateToken(
                userId = aluno.id.value.toString(),
                role = UserRole.ALUNO,
                email = aluno.email
            )
            
            LoginResponse(
                token = token,
                user = UserInfo(
                    id = aluno.id.value.toString(),
                    nome = aluno.nome,
                    email = aluno.email,
                    role = UserRole.ALUNO,
                    fotoPerfilUrl = aluno.fotoPerfilUrl
                )
            )
        }
    }
    
    /**
     * Valida se um aluno tem acesso a um professor específico.
     * Útil para middleware de rotas.
     */
    fun validarAcessoProfessor(alunoId: String, professorId: String): Boolean {
        return transaction {
            InscricaoEntity.find {
                (Inscricoes.alunoId eq UUID.fromString(alunoId)) and
                (Inscricoes.status eq StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true)
            }.any { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
        }
    }
    
    
    // ========== VALIDAÇÕES ==========
    
    private fun validateAlunoLoginRequest(request: AlunoLoginRequest) {
        if (request.email.isBlank()) {
            throw ValidationException("Email é obrigatório", "email")
        }
        if (!request.email.contains("@")) {
            throw ValidationException("Email deve ter formato válido", "email")
        }
        if (request.senha.isBlank()) {
            throw ValidationException("Senha é obrigatória", "senha")
        }
        if (request.professorId.isBlank()) {
            throw ValidationException("ID do professor é obrigatório", "professorId")
        }
    }
    
}

// ========== DTOs ESPECÍFICOS ==========

@Serializable
data class AlunoLoginRequest(
    val email: String,
    val senha: String,
    val professorId: String // ID do professor que o aluno quer acessar
)



@Serializable
data class GerarTokenProfessorRequest(
    val professorId: String
)
