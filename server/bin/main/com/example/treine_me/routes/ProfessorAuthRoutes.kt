package com.example.treine_me.routes

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.dto.LoginRequest
import com.example.treine_me.services.ProfessorAuthService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.professorAuthRoutes() {
    val professorAuthService = ProfessorAuthService()
    
    route("/auth/professor") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = professorAuthService.loginProfessor(request)
            call.respond(ApiResponse.success(response))
        }
    }
}
