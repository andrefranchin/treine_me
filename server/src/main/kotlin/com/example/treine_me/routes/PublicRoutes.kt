package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.services.PublicService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rotas públicas para o app do aluno.
 * Não requerem autenticação, mas sempre precisam do professorId.
 * 
 * IMPORTANTE: Cada professor tem seu próprio app, então todas as rotas
 * são sempre filtradas por professorId passado como parâmetro.
 */
fun Route.publicRoutes() {
    val publicService = PublicService()
    
    route("/public") {
        
        // ========== PROFESSOR ==========
        
        get("/professor/{professorId}") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            
            val response = publicService.getProfessor(professorId)
            call.respond(ApiResponse.success(response))
        }
        
        // ========== PLANOS ==========
        
        get("/professor/{professorId}/planos") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            
            val response = publicService.listPlanos(professorId, page, size)
            call.respond(ApiResponse.success(response))
        }
        
        get("/professor/{professorId}/planos/{planoId}") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val planoId = call.parameters["planoId"] ?: return@get call.respond(
                ApiResponse.error("ID do plano é obrigatório")
            )
            
            val response = publicService.getPlano(planoId, professorId)
            call.respond(ApiResponse.success(response))
        }
        
        get("/professor/{professorId}/planos/{planoId}/produtos") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val planoId = call.parameters["planoId"] ?: return@get call.respond(
                ApiResponse.error("ID do plano é obrigatório")
            )
            
            val response = publicService.listProdutosByPlano(planoId, professorId)
            call.respond(ApiResponse.success(response))
        }
        
        // ========== PRODUTOS ==========
        
        get("/professor/{professorId}/produtos") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            
            val response = publicService.listProdutos(professorId, page, size)
            call.respond(ApiResponse.success(response))
        }
        
        get("/professor/{professorId}/produtos/{produtoId}") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val produtoId = call.parameters["produtoId"] ?: return@get call.respond(
                ApiResponse.error("ID do produto é obrigatório")
            )
            
            val response = publicService.getProduto(produtoId, professorId)
            call.respond(ApiResponse.success(response))
        }
        
        // ========== MÓDULOS ==========
        
        get("/professor/{professorId}/produtos/{produtoId}/modulos") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val produtoId = call.parameters["produtoId"] ?: return@get call.respond(
                ApiResponse.error("ID do produto é obrigatório")
            )
            
            // Token opcional do aluno para verificar permissões e progresso
            val token = call.request.headers["Authorization"]
            
            val response = publicService.listModulosByProduto(produtoId, professorId, token)
            call.respond(ApiResponse.success(response))
        }
        
        get("/professor/{professorId}/modulos/{moduloId}") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val moduloId = call.parameters["moduloId"] ?: return@get call.respond(
                ApiResponse.error("ID do módulo é obrigatório")
            )
            
            // Token opcional do aluno para verificar permissões e progresso
            val token = call.request.headers["Authorization"]
            
            val response = publicService.getModulo(moduloId, professorId, token)
            call.respond(ApiResponse.success(response))
        }
        
        // ========== AULAS ==========
        
        get("/professor/{professorId}/modulos/{moduloId}/aulas") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val moduloId = call.parameters["moduloId"] ?: return@get call.respond(
                ApiResponse.error("ID do módulo é obrigatório")
            )
            
            // Token opcional do aluno para verificar permissões e progresso
            val token = call.request.headers["Authorization"]
            
            val response = publicService.listAulasByModulo(moduloId, professorId, token)
            call.respond(ApiResponse.success(response))
        }
        
        get("/professor/{professorId}/aulas/{aulaId}") {
            val professorId = call.parameters["professorId"] ?: return@get call.respond(
                ApiResponse.error("ID do professor é obrigatório")
            )
            val aulaId = call.parameters["aulaId"] ?: return@get call.respond(
                ApiResponse.error("ID da aula é obrigatório")
            )
            
            // Token opcional do aluno para verificar permissões e progresso
            val token = call.request.headers["Authorization"]
            
            val response = publicService.getAula(aulaId, professorId, token)
            call.respond(ApiResponse.success(response))
        }
    }
}
