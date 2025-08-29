package com.example.treine_me.plugins

import com.example.treine_me.dto.ApiResponse
import com.example.treine_me.exceptions.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error(
                    message = cause.message ?: "Erro de validação",
                    field = cause.field
                )
            )
        }
        
        exception<AuthenticationException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ApiResponse.error(
                    message = cause.message ?: "Falha na autenticação"
                )
            )
        }
        
        exception<AuthorizationException> { call, cause ->
            call.respond(
                HttpStatusCode.Forbidden,
                ApiResponse.error(
                    message = cause.message ?: "Acesso negado"
                )
            )
        }
        
        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ApiResponse.error(
                    message = cause.message ?: "Recurso não encontrado"
                )
            )
        }
        
        exception<ConflictException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ApiResponse.error(
                    message = cause.message ?: "Conflito de dados"
                )
            )
        }
        
        exception<BusinessException> { call, cause ->
            call.respond(
                HttpStatusCode.UnprocessableEntity,
                ApiResponse.error(
                    message = cause.message ?: "Erro de regra de negócio"
                )
            )
        }
        
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse.error(
                    message = "Erro interno do servidor",
                    details = if (call.application.developmentMode) cause.message else null
                )
            )
        }
    }
}
