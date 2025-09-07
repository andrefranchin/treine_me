package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.dto.LoginRequest
import com.example.treine_me.models.AlunoCreateRequest
import com.example.treine_me.models.ProfessorCreateRequest
import com.example.treine_me.services.AuthService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    val authService = AuthService()
    
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = authService.login(request)
            call.respond(ApiResponse.success(response))
        }
        
        post("/register/professor") {
            val request = call.receive<ProfessorCreateRequest>()
            val response = authService.registerProfessor(request)
            call.respond(ApiResponse.success(response))
        }
        
        post("/register/aluno") {
            val request = call.receive<AlunoCreateRequest>()
            val response = authService.registerAluno(request)
            call.respond(ApiResponse.success(response))
        }
    }
}
