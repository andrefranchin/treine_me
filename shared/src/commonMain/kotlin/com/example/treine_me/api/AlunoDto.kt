package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class AlunoCreateRequest(
	val nome: String,
	val email: String,
	val senha: String,
	val fotoPerfilUrl: String? = null,
	val planoId: String // Plano que será atribuído ao aluno automaticamente
)

@Serializable
data class AlunoResponse(
	val id: String,
	val nome: String,
	val email: String,
	val fotoPerfilUrl: String? = null,
	val role: UserRole = UserRole.ALUNO
)


