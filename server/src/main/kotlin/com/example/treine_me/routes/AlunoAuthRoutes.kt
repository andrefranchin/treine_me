package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.dto.LoginRequest
import com.example.treine_me.enums.UserRole
import com.example.treine_me.services.AlunoAuthService
import com.example.treine_me.services.AlunoLoginRequest
import com.example.treine_me.services.GerarTokenProfessorRequest
import com.example.treine_me.plugins.requireRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rotas de autenticação específicas para alunos.
 * Inclui login com professor específico e gestão de contexto.
 */
fun Route.alunoAuthRoutes() {
    val alunoAuthService = AlunoAuthService()
    
    route("/auth/aluno") {
        
        // Login do aluno especificando o professor
        post("/login") {
            val request = call.receive<AlunoLoginRequest>()
            val response = alunoAuthService.loginAluno(request)
            call.respond(ApiResponse.success(response))
        }
        
        
        // Gerar token para professor específico (após escolha)
        post("/gerar-token-professor") {
            val request = call.receive<GerarTokenProfessorRequest>()
            val alunoId = call.request.queryParameters["alunoId"] ?: return@post call.respond(
                ApiResponse.error("ID do aluno é obrigatório")
            )
            
            val response = alunoAuthService.gerarTokenProfessor(alunoId, request.professorId)
            call.respond(ApiResponse.success(response))
        }
    }
    
    // Rotas autenticadas para validação de acesso
    authenticate("auth-jwt") {
        route("/aluno/contexto") {
            
            post("/validar-acesso-professor/{professorId}") {
                call.requireRole(UserRole.ALUNO)
                val professorId = call.parameters["professorId"] ?: return@post call.respond(
                    ApiResponse.error("ID do professor é obrigatório")
                )
                val principal = call.principal<JWTPrincipal>()
                val alunoId = principal!!.payload.getClaim("userId").asString()
                
                val temAcesso = alunoAuthService.validarAcessoProfessor(alunoId, professorId)
                call.respond(ApiResponse.success(mapOf(
                    "temAcesso" to temAcesso,
                    "professorId" to professorId
                )))
            }
        }
    }
}
