package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class ProdutoCreateRequest(
    val titulo: String,
    val descricao: String,
    val tipo: TipoProduto,
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null
)

@Serializable
data class ProdutoUpdateRequest(
    val titulo: String? = null,
    val descricao: String? = null,
    val tipo: TipoProduto? = null,
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null
)

@Serializable
data class ProdutoResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val tipo: TipoProduto,
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null,
    val professorId: String,
    val professorNome: String
)


