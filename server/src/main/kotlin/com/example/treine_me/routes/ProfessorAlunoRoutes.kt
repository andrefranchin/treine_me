package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.enums.UserRole
import com.example.treine_me.models.AlunoCreateRequest
import com.example.treine_me.models.AlunoUpdateRequest
import com.example.treine_me.services.AlunoCrudService
import com.example.treine_me.services.ProgressoAulaService
import com.example.treine_me.services.ResetAlunoPasswordRequest
import com.example.treine_me.plugins.requireRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rotas para professores gerenciarem seus alunos e visualizarem progresso.
 * Requerem token JWT e role PROFESSOR.
 */
fun Route.professorAlunoRoutes() {
    val alunoCrudService = AlunoCrudService()
    val progressoAulaService = ProgressoAulaService()
    
    authenticate("auth-jwt") {
        route("/professores/me") {
            
            // ========== CRUD DE ALUNOS ==========
            
            route("/alunos") {
                post {
                    call.requireRole(UserRole.PROFESSOR)
                    val request = call.receive<AlunoCreateRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = alunoCrudService.createAluno(request, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                get {
                    call.requireRole(UserRole.PROFESSOR)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = alunoCrudService.listAlunosByProfessor(professorId, page, size)
                    call.respond(ApiResponse.success(response))
                }
                
                get("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val alunoId = call.parameters["id"] ?: return@get call.respond(
                        ApiResponse.error("ID do aluno é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = alunoCrudService.getAluno(alunoId, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                put("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val alunoId = call.parameters["id"] ?: return@put call.respond(
                        ApiResponse.error("ID do aluno é obrigatório")
                    )
                    val request = call.receive<AlunoUpdateRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = alunoCrudService.updateAluno(alunoId, request, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                delete("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val alunoId = call.parameters["id"] ?: return@delete call.respond(
                        ApiResponse.error("ID do aluno é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val success = alunoCrudService.deactivateAluno(alunoId, professorId)
                    call.respond(ApiResponse.success(mapOf("success" to success)))
                }
                
                post("/{id}/reset-password") {
                    call.requireRole(UserRole.PROFESSOR)
                    val alunoId = call.parameters["id"] ?: return@post call.respond(
                        ApiResponse.error("ID do aluno é obrigatório")
                    )
                    val request = call.receive<ResetAlunoPasswordRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val success = alunoCrudService.resetAlunoPassword(alunoId, request.newPassword, professorId)
                    call.respond(ApiResponse.success(mapOf("success" to success)))
                }
            }
            
            // ========== PROGRESSO DOS ALUNOS ==========
            
            route("/alunos/{alunoId}/progresso") {
                
                get("/resumo") {
                    call.requireRole(UserRole.PROFESSOR)
                    val alunoId = call.parameters["alunoId"] ?: return@get call.respond(
                        ApiResponse.error("ID do aluno é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = progressoAulaService.getProgressoAluno(alunoId, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                get("/por-modulo") {
                    call.requireRole(UserRole.PROFESSOR)
                    val alunoId = call.parameters["alunoId"] ?: return@get call.respond(
                        ApiResponse.error("ID do aluno é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = progressoAulaService.getProgressoPorModulo(alunoId, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                get("/detalhado") {
                    call.requireRole(UserRole.PROFESSOR)
                    val alunoId = call.parameters["alunoId"] ?: return@get call.respond(
                        ApiResponse.error("ID do aluno é obrigatório")
                    )
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = progressoAulaService.getProgressoDetalhado(alunoId, professorId, page, size)
                    call.respond(ApiResponse.success(response))
                }
            }
            
            // ========== RELATÓRIOS DE PROGRESSO ==========
            
            get("/relatorio-progresso") {
                call.requireRole(UserRole.PROFESSOR)
                val principal = call.principal<JWTPrincipal>()
                val professorId = principal!!.payload.getClaim("userId").asString()
                
                // Buscar todos os alunos e seus resumos de progresso
                val alunos = alunoCrudService.listAlunosByProfessor(professorId, 1, 100)
                val relatorio = alunos.data.map { aluno ->
                    val resumoProgresso = progressoAulaService.getProgressoAluno(aluno.id, professorId)
                    mapOf(
                        "aluno" to aluno,
                        "progresso" to resumoProgresso
                    )
                }
                
                call.respond(ApiResponse.success(mapOf(
                    "relatorio" to relatorio,
                    "totalAlunos" to alunos.total
                )))
            }
            
            get("/estatisticas-gerais") {
                call.requireRole(UserRole.PROFESSOR)
                val principal = call.principal<JWTPrincipal>()
                val professorId = principal!!.payload.getClaim("userId").asString()
                
                // Buscar estatísticas gerais
                val alunos = alunoCrudService.listAlunosByProfessor(professorId, 1, 1000) // Todos os alunos
                val totalAlunos = alunos.total.toInt()
                
                if (totalAlunos == 0) {
                    call.respond(ApiResponse.success(mapOf(
                        "totalAlunos" to 0,
                        "totalMinutosAssistidos" to 0,
                        "mediaProgressoPorAluno" to 0,
                        "alunosAtivos" to 0,
                        "aulasMaisAssistidas" to emptyList<Any>()
                    )))
                    return@get
                }
                
                val resumosProgresso = alunos.data.map { aluno ->
                    try {
                        progressoAulaService.getProgressoAluno(aluno.id, professorId)
                    } catch (e: Exception) {
                        null
                    }
                }.filterNotNull()
                
                val totalMinutosAssistidos = resumosProgresso.sumOf { it.minutosTotaisAssistidos }
                val mediaProgresso = if (resumosProgresso.isNotEmpty()) {
                    resumosProgresso.map { it.percentualGeralConcluido }.average().toInt()
                } else 0
                val alunosAtivos = resumosProgresso.count { it.ultimaAtividade != null }
                
                call.respond(ApiResponse.success(mapOf(
                    "totalAlunos" to totalAlunos,
                    "totalMinutosAssistidos" to totalMinutosAssistidos,
                    "mediaProgressoPorAluno" to mediaProgresso,
                    "alunosAtivos" to alunosAtivos,
                    "percentualAlunosAtivos" to if (totalAlunos > 0) (alunosAtivos * 100) / totalAlunos else 0
                )))
            }
        }
    }
}
