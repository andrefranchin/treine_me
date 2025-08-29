package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
	val email: String,
	val senha: String
)

@Serializable
data class UserInfo(
	val id: String,
	val nome: String,
	val email: String,
	val role: UserRole,
	val fotoPerfilUrl: String? = null
)

@Serializable
data class LoginResponse(
	val token: String,
	val user: UserInfo
)


