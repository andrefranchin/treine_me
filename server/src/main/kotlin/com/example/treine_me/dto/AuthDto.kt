package com.example.treine_me.dto

import com.example.treine_me.api.UserRole
import kotlinx.serialization.Serializable

// Reexport shared models with original names to keep server imports working
typealias LoginRequest = com.example.treine_me.api.LoginRequest
typealias LoginResponse = com.example.treine_me.api.LoginResponse
typealias UserInfo = com.example.treine_me.api.UserInfo

@Serializable
data class JwtPayload(
    val userId: String,
    val role: UserRole,
    val email: String
)
