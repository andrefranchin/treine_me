package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class ModuloCreateRequest(
    val titulo: String,
    val descricao: String,
    val ordem: Int? = null, // Opcional - será calculado automaticamente se não fornecido
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null
)

@Serializable
data class ModuloUpdateRequest(
    val titulo: String? = null,
    val descricao: String? = null,
    val ordem: Int? = null,
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null
)

@Serializable
data class ConteudoResponse(
    val id: String,
    val urlVideo: String? = null,
    val textoMarkdown: String? = null,
    val arquivoUrl: String? = null,
    val aulaId: String
)

@Serializable
data class AulaResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: TipoConteudo,
    val planoId: String,
    val moduloId: String,
    val conteudo: ConteudoResponse? = null
)

@Serializable
data class ModuloResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null,
    val produtoId: String,
    val aulas: List<AulaResponse> = emptyList()
)

@Serializable
data class AulaCreateRequest(
    val titulo: String,
    val descricao: String,
    val ordem: Int? = null, // Opcional - será calculado automaticamente se não fornecido
    val tipoConteudo: TipoConteudo,
    val planoId: String
)

@Serializable
data class AulaUpdateRequest(
    val titulo: String? = null,
    val descricao: String? = null,
    val ordem: Int? = null,
    val tipoConteudo: TipoConteudo? = null,
    val planoId: String? = null
)

@Serializable
data class ConteudoUpdateRequest(
    val urlVideo: String? = null, // URL do vídeo após upload
    val textoMarkdown: String? = null, // Conteúdo HTML do rich text editor
    val arquivoUrl: String? = null // URL do arquivo complementar após upload
)


