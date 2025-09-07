package com.example.treine_me.services

import com.example.treine_me.auth.PasswordHelper
import com.example.treine_me.exceptions.ConflictException
import com.example.treine_me.exceptions.NotFoundException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AdminService {
    
    fun createProfessor(request: ProfessorCreateRequest, adminId: String): ProfessorResponse {
        validateProfessorRequest(request)
        
        return transaction {
            // Verificar se email já existe
            val existingProfessor = ProfessorEntity.find { Professores.email eq request.email }.firstOrNull()
            val existingAluno = AlunoEntity.find { Alunos.email eq request.email }.firstOrNull()
            val existingAdmin = AdminEntity.find { Admins.email eq request.email }.firstOrNull()
            
            if (existingProfessor != null || existingAluno != null || existingAdmin != null) {
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
                idUserIns = UUID.fromString(adminId)
                idUserUpd = UUID.fromString(adminId)
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
    
    fun listProfessores(page: Int = 1, size: Int = 20): PaginatedResponse<ProfessorResponse> {
        return transaction {
            val offset = (page - 1) * size
            
            val professores = ProfessorEntity.find { Professores.isActive eq true }
                .drop(offset)
                .take(size)
                .map { professor ->
                    ProfessorResponse(
                        id = professor.id.value.toString(),
                        nome = professor.nome,
                        email = professor.email,
                        bio = professor.bio,
                        fotoPerfilUrl = professor.fotoPerfilUrl
                    )
                }
            
            val total = ProfessorEntity.find { Professores.isActive eq true }.count()
            
            PaginatedResponse(
                data = professores,
                page = page,
                size = size,
                total = total,
                totalPages = (total + size - 1) / size
            )
        }
    }
    
    fun getProfessor(professorId: String): ProfessorResponse {
        return transaction {
            val professor = ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
            
            ProfessorResponse(
                id = professor.id.value.toString(),
                nome = professor.nome,
                email = professor.email,
                bio = professor.bio,
                fotoPerfilUrl = professor.fotoPerfilUrl
            )
        }
    }
    
    fun updateProfessor(professorId: String, request: ProfessorUpdateRequest, adminId: String): ProfessorResponse {
        return transaction {
            val professor = ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
            
            val now = Clock.System.now()
            
            request.nome?.let { professor.nome = it }
            request.bio?.let { professor.bio = it }
            request.fotoPerfilUrl?.let { professor.fotoPerfilUrl = it }
            
            professor.dtUpd = now
            professor.idUserUpd = UUID.fromString(adminId)
            
            ProfessorResponse(
                id = professor.id.value.toString(),
                nome = professor.nome,
                email = professor.email,
                bio = professor.bio,
                fotoPerfilUrl = professor.fotoPerfilUrl
            )
        }
    }
    
    fun deactivateProfessor(professorId: String, adminId: String): Boolean {
        return transaction {
            val professor = ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
            
            val now = Clock.System.now()
            professor.isActive = false
            professor.dtUpd = now
            professor.idUserUpd = UUID.fromString(adminId)
            
            true
        }
    }
    
    fun resetProfessorPassword(professorId: String, newPassword: String, adminId: String): Boolean {
        if (newPassword.length < 6) {
            throw ValidationException("Senha deve ter pelo menos 6 caracteres", "senha")
        }
        
        return transaction {
            val professor = ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
            
            val now = Clock.System.now()
            professor.senhaHash = PasswordHelper.hashPassword(newPassword)
            professor.dtUpd = now
            professor.idUserUpd = UUID.fromString(adminId)
            
            true
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
}

@kotlinx.serialization.Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
    val totalPages: Long
)

@kotlinx.serialization.Serializable
data class ResetPasswordRequest(
    val newPassword: String
)
