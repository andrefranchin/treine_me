package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.enums.UserRole
import com.example.treine_me.models.AlunoUpdateRequest
import com.example.treine_me.services.AlunoService
import com.example.treine_me.plugins.requireRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rotas específicas para alunos autenticados.
 * Requerem token JWT e role ALUNO.
 * Sempre filtradas por professorId para garantir que o aluno só acesse
 * conteúdo do professor específico.
 */
fun Route.alunoRoutes() {
    val alunoService = AlunoService()
    
    authenticate("auth-jwt") {
        route("/aluno") {
            
            // ========== PERFIL ==========
            
            get("/me") {
                call.requireRole(UserRole.ALUNO)
                val principal = call.principal<JWTPrincipal>()
                val alunoId = principal!!.payload.getClaim("userId").asString()
                
                val response = alunoService.getMeuPerfil(alunoId)
                call.respond(ApiResponse.success(response))
            }
            
            put("/me") {
                call.requireRole(UserRole.ALUNO)
                val request = call.receive<AlunoUpdateRequest>()
                val principal = call.principal<JWTPrincipal>()
                val alunoId = principal!!.payload.getClaim("userId").asString()
                
                val response = alunoService.updateMeuPerfil(alunoId, request)
                call.respond(ApiResponse.success(response))
            }
            
            // ========== INSCRIÇÕES ==========
            
            get("/inscricoes") {
                call.requireRole(UserRole.ALUNO)
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val principal = call.principal<JWTPrincipal>()
                val alunoId = principal!!.payload.getClaim("userId").asString()
                
                val response = alunoService.getMinhasInscricoes(alunoId, page, size)
                call.respond(ApiResponse.success(response))
            }
            
            get("/inscricoes/{inscricaoId}") {
                call.requireRole(UserRole.ALUNO)
                val inscricaoId = call.parameters["inscricaoId"] ?: return@get call.respond(
                    ApiResponse.error("ID da inscrição é obrigatório")
                )
                val principal = call.principal<JWTPrincipal>()
                val alunoId = principal!!.payload.getClaim("userId").asString()
                
                val response = alunoService.getInscricao(inscricaoId, alunoId)
                call.respond(ApiResponse.success(response))
            }
            
            // ========== ACESSO A CONTEÚDO (por professor) ==========
            
            get("/professor/{professorId}/produtos") {
                call.requireRole(UserRole.ALUNO)
                val professorId = call.parameters["professorId"] ?: return@get call.respond(
                    ApiResponse.error("ID do professor é obrigatório")
                )
                val principal = call.principal<JWTPrincipal>()
                val alunoId = principal!!.payload.getClaim("userId").asString()
                
                val response = alunoService.getProdutosDisponiveis(alunoId, professorId)
                call.respond(ApiResponse.success(response))
            }
            
            get("/professor/{professorId}/aulas/{aulaId}") {
                call.requireRole(UserRole.ALUNO)
                val professorId = call.parameters["professorId"] ?: return@get call.respond(
                    ApiResponse.error("ID do professor é obrigatório")
                )
                val aulaId = call.parameters["aulaId"] ?: return@get call.respond(
                    ApiResponse.error("ID da aula é obrigatório")
                )
                val principal = call.principal<JWTPrincipal>()
                val alunoId = principal!!.payload.getClaim("userId").asString()
                
                val response = alunoService.getAulaCompleta(aulaId, alunoId, professorId)
                call.respond(ApiResponse.success(response))
            }
            
            get("/professor/{professorId}/aulas/{aulaId}/conteudo") {
                call.requireRole(UserRole.ALUNO)
                val professorId = call.parameters["professorId"] ?: return@get call.respond(
                    ApiResponse.error("ID do professor é obrigatório")
                )
                val aulaId = call.parameters["aulaId"] ?: return@get call.respond(
                    ApiResponse.error("ID da aula é obrigatório")
                )
                val principal = call.principal<JWTPrincipal>()
                val alunoId = principal!!.payload.getClaim("userId").asString()
                
                val response = alunoService.getConteudoAula(aulaId, alunoId, professorId)
                call.respond(ApiResponse.success(response))
            }
        }
    }
}
