package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class PlanoCreateRequest(
    val nome: String,
    val descricao: String,
    val valor: String,
    val recorrencia: Recorrencia
)

@Serializable
data class PlanoUpdateRequest(
    val nome: String? = null,
    val descricao: String? = null,
    val valor: String? = null,
    val recorrencia: Recorrencia? = null
)

@Serializable
data class PlanoResponse(
    val id: String,
    val nome: String,
    val descricao: String,
    val valor: String,
    val recorrencia: Recorrencia,
    val professorId: String,
    val professorNome: String
)
