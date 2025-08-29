package com.example.treine_me.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.treine_me.dto.JwtPayload
import com.example.treine_me.enums.UserRole
import kotlinx.datetime.Clock
import kotlinx.datetime.plus
import kotlin.time.Duration.Companion.days

object JwtConfig {
    private const val SECRET = "your-jwt-secret-key-change-in-production"
    private const val ISSUER = "treine_me"
    private const val AUDIENCE = "treine_me_users"
    private val ALGORITHM = Algorithm.HMAC256(SECRET)
    
    fun generateToken(userId: String, role: UserRole, email: String): String {
        val now = Clock.System.now()
        val expiration = now.plus(7.days)
        
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("userId", userId)
            .withClaim("role", role.name)
            .withClaim("email", email)
            .withIssuedAt(now.toEpochMilliseconds().let { java.util.Date(it) })
            .withExpiresAt(expiration.toEpochMilliseconds().let { java.util.Date(it) })
            .sign(ALGORITHM)
    }
    
    fun verifyToken(token: String): DecodedJWT? {
        return try {
            JWT.require(ALGORITHM)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .build()
                .verify(token)
        } catch (e: Exception) {
            null
        }
    }
    
    fun extractPayload(token: String): JwtPayload? {
        val decoded = verifyToken(token) ?: return null
        
        return try {
            JwtPayload(
                userId = decoded.getClaim("userId").asString(),
                role = UserRole.valueOf(decoded.getClaim("role").asString()),
                email = decoded.getClaim("email").asString()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun getSecret() = SECRET
    fun getIssuer() = ISSUER
    fun getAudience() = AUDIENCE
}
