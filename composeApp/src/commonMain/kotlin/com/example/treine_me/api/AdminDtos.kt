package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class ProfessorUpdateRequest(
    val nome: String? = null,
    val bio: String? = null,
    val fotoPerfilUrl: String? = null
)

@Serializable
data class ResetPasswordRequest(
    val newPassword: String
)


