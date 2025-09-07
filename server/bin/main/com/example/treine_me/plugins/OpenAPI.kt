package com.example.treine_me.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureOpenAPI() {
    routing {
        // Swagger UI - Interface web interativa
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
        
        // Endpoint para servir a documentação OpenAPI em formato YAML
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    }
}
