package com.example.treine_me.services

import com.example.treine_me.auth.JwtConfig
import com.example.treine_me.auth.PasswordHelper
import com.example.treine_me.dto.LoginRequest
import com.example.treine_me.dto.LoginResponse
import com.example.treine_me.dto.UserInfo
import com.example.treine_me.enums.UserRole
import com.example.treine_me.exceptions.AuthenticationException
import com.example.treine_me.exceptions.ConflictException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AuthService {
    
    fun login(request: LoginRequest): LoginResponse {
        validateLoginRequest(request)
        
        return transaction {
            // Tentar encontrar professor primeiro
            val professor = ProfessorEntity.find { 
                (Professores.email eq request.email) and (Professores.isActive eq true) 
            }.firstOrNull()
            
            if (professor != null) {
                if (PasswordHelper.verifyPassword(request.senha, professor.senhaHash)) {
                    val token = JwtConfig.generateToken(
                        userId = professor.id.value.toString(),
                        role = UserRole.PROFESSOR,
                        email = professor.email
                    )
                    
                    return@transaction LoginResponse(
                        token = token,
                        user = UserInfo(
                            id = professor.id.value.toString(),
                            nome = professor.nome,
                            email = professor.email,
                            role = UserRole.PROFESSOR,
                            fotoPerfilUrl = professor.fotoPerfilUrl
                        )
                    )
                }
            }
            
            // Tentar encontrar aluno
            val aluno = AlunoEntity.find { 
                (Alunos.email eq request.email) and (Alunos.isActive eq true) 
            }.firstOrNull()
            
            if (aluno != null) {
                if (PasswordHelper.verifyPassword(request.senha, aluno.senhaHash)) {
                    val token = JwtConfig.generateToken(
                        userId = aluno.id.value.toString(),
                        role = UserRole.ALUNO,
                        email = aluno.email
                    )
                    
                    return@transaction LoginResponse(
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
            
            throw AuthenticationException("Email ou senha inválidos")
        }
    }
    
    fun registerProfessor(request: ProfessorCreateRequest): ProfessorResponse {
        validateProfessorRequest(request)
        
        return transaction {
            // Verificar se email já existe
            val existingProfessor = ProfessorEntity.find { Professores.email eq request.email }.firstOrNull()
            val existingAluno = AlunoEntity.find { Alunos.email eq request.email }.firstOrNull()
            
            if (existingProfessor != null || existingAluno != null) {
                throw ConflictException("Email já está em uso")
            }
            
            val now = Clock.System.now()
            val professor = ProfessorEntity.new {
                nome = request.nome
                email = request.email
                senhaHash = PasswordHelper.hashPassword(request.senha)
                bio = request.bio
                fotoPerfilUrl = request.fotoPerfilUrl
                dtIns = now
                dtUpd = now
                idUserIns = UUID.randomUUID() // Sistema
                idUserUpd = UUID.randomUUID() // Sistema
                isActive = true
            }
            
            ProfessorResponse(
                id = professor.id.value.toString(),
                nome = professor.nome,
                email = professor.email,
                bio = professor.bio,
                fotoPerfilUrl = professor.fotoPerfilUrl
            )
        }
    }
    
    fun registerAluno(request: AlunoCreateRequest): AlunoResponse {
        validateAlunoRequest(request)
        
        return transaction {
            // Verificar se email já existe
            val existingProfessor = ProfessorEntity.find { Professores.email eq request.email }.firstOrNull()
            val existingAluno = AlunoEntity.find { Alunos.email eq request.email }.firstOrNull()
            
            if (existingProfessor != null || existingAluno != null) {
                throw ConflictException("Email já está em uso")
            }
            
            val now = Clock.System.now()
            val aluno = AlunoEntity.new {
                nome = request.nome
                email = request.email
                senhaHash = PasswordHelper.hashPassword(request.senha)
                fotoPerfilUrl = request.fotoPerfilUrl
                dtIns = now
                dtUpd = now
                idUserIns = UUID.randomUUID() // Sistema
                idUserUpd = UUID.randomUUID() // Sistema
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
    
    private fun validateLoginRequest(request: LoginRequest) {
        if (request.email.isBlank()) {
            throw ValidationException("Email é obrigatório", "email")
        }
        if (!request.email.contains("@")) {
            throw ValidationException("Email deve ter formato válido", "email")
        }
        if (request.senha.isBlank()) {
            throw ValidationException("Senha é obrigatória", "senha")
        }
    }
    
    private fun validateProfessorRequest(request: ProfessorCreateRequest) {
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
    }
    
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
    }
}
