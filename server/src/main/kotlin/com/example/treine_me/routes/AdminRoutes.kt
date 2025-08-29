package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.enums.UserRole
import com.example.treine_me.models.ProfessorCreateRequest
import com.example.treine_me.models.ProfessorUpdateRequest
import com.example.treine_me.services.AdminService
import com.example.treine_me.services.ResetPasswordRequest
import com.example.treine_me.plugins.requireRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Route.adminRoutes() {
    val adminService = AdminService()

    authenticate("auth-jwt") {
        route("/admin") {
            // Verifica a role após autenticação
            intercept(ApplicationCallPipeline.Call) {
                call.requireRole(UserRole.ADMIN)
            }
            
            route("/professores") {
                post {
                    val request = call.receive<ProfessorCreateRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val adminId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = adminService.createProfessor(request, adminId)
                    call.respond(ApiResponse.success(response))
                }
                
                get {
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                    
                    val response = adminService.listProfessores(page, size)
                    call.respond(ApiResponse.success(response))
                }
                
                get("/{id}") {
                    val professorId = call.parameters["id"] ?: return@get call.respond(
                        ApiResponse.error("ID do professor é obrigatório")
                    )
                    
                    val response = adminService.getProfessor(professorId)
                    call.respond(ApiResponse.success(response))
                }
                
                put("/{id}") {
                    val professorId = call.parameters["id"] ?: return@put call.respond(
                        ApiResponse.error("ID do professor é obrigatório")
                    )
                    
                    val request = call.receive<ProfessorUpdateRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val adminId = principal!!.payload.getClaim("userId").asString()
                    
                    val response = adminService.updateProfessor(professorId, request, adminId)
                    call.respond(ApiResponse.success(response))
                }
                
                delete("/{id}") {
                    val professorId = call.parameters["id"] ?: return@delete call.respond(
                        ApiResponse.error("ID do professor é obrigatório")
                    )
                    
                    val principal = call.principal<JWTPrincipal>()
                    val adminId = principal!!.payload.getClaim("userId").asString()
                    
                    val success = adminService.deactivateProfessor(professorId, adminId)
                    call.respond(ApiResponse.success(mapOf("success" to success)))
                }
                
                post("/{id}/reset-password") {
                    val professorId = call.parameters["id"] ?: return@post call.respond(
                        ApiResponse.error("ID do professor é obrigatório")
                    )
                    
                    val request = call.receive<ResetPasswordRequest>()
                    val principal = call.principal<JWTPrincipal>()
                    val adminId = principal!!.payload.getClaim("userId").asString()
                    
                    val success = adminService.resetProfessorPassword(professorId, request.newPassword, adminId)
                    call.respond(ApiResponse.success(mapOf("success" to success)))
                }
            }
        }
    }
}
