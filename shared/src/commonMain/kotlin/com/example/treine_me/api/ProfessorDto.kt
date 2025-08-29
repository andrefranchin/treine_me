package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class ProfessorCreateRequest(
	val nome: String,
	val email: String,
	val senha: String,
	val bio: String? = null,
	val fotoPerfilUrl: String? = null
)

@Serializable
data class ProfessorResponse(
	val id: String,
	val nome: String,
	val email: String,
	val bio: String? = null,
	val fotoPerfilUrl: String? = null
)


