package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.enums.UserRole
import com.example.treine_me.models.*
import com.example.treine_me.services.ProfessorService
import com.example.treine_me.plugins.requireRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.professorRoutes() {
    val professorService = ProfessorService()
    
    authenticate("auth-jwt") {
        route("/professores/me") {
            
            // ========== PLANOS ==========
            route("/planos") {
                post {
                    call.requireRole(UserRole.PROFESSOR)
                    val request = call.receive<PlanoCreateRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.createPlano(request, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                get {
                    call.requireRole(UserRole.PROFESSOR)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.listPlanos(professorId, page, size)
                    call.respond(ApiResponse.success(response))
                }
                
                get("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val planoId = call.parameters["id"] ?: return@get call.respond(
                        ApiResponse.error("ID do plano é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.getPlano(planoId, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                put("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val planoId = call.parameters["id"] ?: return@put call.respond(
                        ApiResponse.error("ID do plano é obrigatório")
                    )
                    val request = call.receive<PlanoUpdateRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.updatePlano(planoId, request, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                delete("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val planoId = call.parameters["id"] ?: return@delete call.respond(
                        ApiResponse.error("ID do plano é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val success = professorService.deactivatePlano(planoId, professorId)
                    call.respond(ApiResponse.success(mapOf("success" to success)))
                }
                
                // Produtos do plano
                get("/{id}/produtos") {
                    call.requireRole(UserRole.PROFESSOR)
                    val planoId = call.parameters["id"] ?: return@get call.respond(
                        ApiResponse.error("ID do plano é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.listProdutosByPlano(planoId, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                post("/{planoId}/produtos/{produtoId}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val planoId = call.parameters["planoId"] ?: return@post call.respond(
                        ApiResponse.error("ID do plano é obrigatório")
                    )
                    val produtoId = call.parameters["produtoId"] ?: return@post call.respond(
                        ApiResponse.error("ID do produto é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val success = professorService.addProdutoToPlano(planoId, produtoId, professorId)
                    call.respond(ApiResponse.success(mapOf("success" to success)))
                }
                
                delete("/{planoId}/produtos/{produtoId}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val planoId = call.parameters["planoId"] ?: return@delete call.respond(
                        ApiResponse.error("ID do plano é obrigatório")
                    )
                    val produtoId = call.parameters["produtoId"] ?: return@delete call.respond(
                        ApiResponse.error("ID do produto é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val success = professorService.removeProdutoFromPlano(planoId, produtoId, professorId)
                    call.respond(ApiResponse.success(mapOf("success" to success)))
                }
                
                // Atribuir plano a aluno
                post("/{planoId}/alunos/{alunoId}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val planoId = call.parameters["planoId"] ?: return@post call.respond(
                        ApiResponse.error("ID do plano é obrigatório")
                    )
                    val alunoId = call.parameters["alunoId"] ?: return@post call.respond(
                        ApiResponse.error("ID do aluno é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.assignPlanoToAluno(planoId, alunoId, professorId)
                    call.respond(ApiResponse.success(response))
                }
            }
            
            // ========== PRODUTOS ==========
            route("/produtos") {
                post {
                    call.requireRole(UserRole.PROFESSOR)
                    val request = call.receive<ProdutoCreateRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.createProduto(request, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                get {
                    call.requireRole(UserRole.PROFESSOR)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.listProdutos(professorId, page, size)
                    call.respond(ApiResponse.success(response))
                }
                
                get("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val produtoId = call.parameters["id"] ?: return@get call.respond(
                        ApiResponse.error("ID do produto é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.getProduto(produtoId, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                put("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val produtoId = call.parameters["id"] ?: return@put call.respond(
                        ApiResponse.error("ID do produto é obrigatório")
                    )
                    val request = call.receive<ProdutoUpdateRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = professorService.updateProduto(produtoId, request, professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                delete("/{id}") {
                    call.requireRole(UserRole.PROFESSOR)
                    val produtoId = call.parameters["id"] ?: return@delete call.respond(
                        ApiResponse.error("ID do produto é obrigatório")
                    )
                    val principal = call.principal<JWTPrincipal>()
                    val professorId = principal!!.payload.getClaim("userId").asString()
                    
                    val success = professorService.deactivateProduto(produtoId, professorId)
                    call.respond(ApiResponse.success(mapOf("success" to success)))
                }
            }
        }
    }
}
