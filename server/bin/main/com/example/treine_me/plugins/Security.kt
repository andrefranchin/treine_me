package com.example.treine_me.plugins

import com.example.treine_me.auth.JwtConfig
import com.example.treine_me.dto.JwtPayload
import com.example.treine_me.enums.UserRole
import com.example.treine_me.exceptions.AuthenticationException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Treine Me"
            verifier {
                com.auth0.jwt.JWT
                    .require(com.auth0.jwt.algorithms.Algorithm.HMAC256(JwtConfig.getSecret()))
                    .withIssuer(JwtConfig.getIssuer())
                    .withAudience(JwtConfig.getAudience())
                    .build()
            }
            validate { credential ->
                // Check if the token has the required claims
                try {
                    val userId = credential.payload.getClaim("userId")?.asString()
                    val role = credential.payload.getClaim("role")?.asString()
                    val email = credential.payload.getClaim("email")?.asString()
                    
                    if (userId != null && role != null && email != null) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            challenge { _, _ ->
                throw AuthenticationException("Token inválido ou expirado")
            }
        }
    }
}

fun ApplicationCall.getJwtPayload(): JwtPayload {
    val principal = authentication.principal<JWTPrincipal>()
        ?: throw AuthenticationException("Token não encontrado")
    
    // Extract claims directly from the payload
    val userId = principal.payload.getClaim("userId").asString()
    val role = UserRole.valueOf(principal.payload.getClaim("role").asString())
    val email = principal.payload.getClaim("email").asString()
    
    return JwtPayload(userId = userId, role = role, email = email)
}

fun ApplicationCall.requireRole(requiredRole: UserRole) {
    val payload = getJwtPayload()
    if (payload.role != requiredRole) {
        throw com.example.treine_me.exceptions.AuthorizationException("Acesso negado para esta operação")
    }
}
