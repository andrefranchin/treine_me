package com.example.treine_me.services

import com.example.treine_me.auth.JwtConfig
import com.example.treine_me.auth.PasswordHelper
import com.example.treine_me.dto.LoginRequest
import com.example.treine_me.dto.LoginResponse
import com.example.treine_me.dto.UserInfo
import com.example.treine_me.enums.UserRole
import com.example.treine_me.exceptions.AuthenticationException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.Professores
import com.example.treine_me.models.ProfessorEntity
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class ProfessorAuthService {
    
    fun loginProfessor(request: LoginRequest): LoginResponse {
        validateLoginRequest(request)
        
        return transaction {
            // Buscar apenas professor
            val professor = ProfessorEntity.find { 
                (Professores.email eq request.email) and (Professores.isActive eq true) 
            }.firstOrNull()
            
            if (professor == null) {
                throw AuthenticationException("Professor não encontrado com este email")
            }
            
            if (!PasswordHelper.verifyPassword(request.senha, professor.senhaHash)) {
                throw AuthenticationException("Senha incorreta")
            }
            
            val token = JwtConfig.generateToken(
                userId = professor.id.value.toString(),
                role = UserRole.PROFESSOR,
                email = professor.email
            )
            
            LoginResponse(
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
}
