package com.example.treine_me

import com.example.treine_me.database.DatabaseConfig
import com.example.treine_me.plugins.*
import com.example.treine_me.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Inicializar banco de dados
    DatabaseConfig.init()
    
    // Configurar plugins
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureStatusPages()
    
    // Configurar rotas
    routing {
        get("/") {
            call.respondText("Treine Me API - Hello World!")
        }
        
        get("/health") {
            call.respond(mapOf("status" to "OK", "message" to "API está funcionando"))
        }
        
        authRoutes()
    }
}