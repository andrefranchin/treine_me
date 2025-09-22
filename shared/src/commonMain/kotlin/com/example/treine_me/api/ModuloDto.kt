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
    val conteudo: ConteudoResponse? = null,
    
    // Metadados do vídeo (preenchidos automaticamente)
    val videoDuracaoSegundos: Int? = null,
    val videoResolucao: String? = null,
    val videoTamanhoBytes: Long? = null,
    val videoCodec: String? = null,
    val videoFps: Int? = null,
    val videoAspectRatio: String? = null,
    
    // Configurações do treino
    val caloriasPerdidas: Int? = null,
    val dificuldade: DificuldadeTreino? = null,
    val tipoTreino: TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null,
    val observacoesTreino: String? = null
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
data class PublicAulaResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: TipoConteudo,
    val planoId: String,
    val moduloId: String,
    val temConteudo: Boolean
)

@Serializable
data class AulaCreateRequest(
    val titulo: String,
    val descricao: String,
    val ordem: Int? = null, // Opcional - será calculado automaticamente se não fornecido
    val tipoConteudo: TipoConteudo,
    val planoId: String,
    
    // Configurações do treino (campos do formulário)
    val caloriasPerdidas: Int? = null,
    val dificuldade: DificuldadeTreino? = null,
    val tipoTreino: TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null, // Escala de 1-10
    val observacoesTreino: String? = null
)

@Serializable
data class AulaUpdateRequest(
    val titulo: String? = null,
    val descricao: String? = null,
    val ordem: Int? = null,
    val tipoConteudo: TipoConteudo? = null,
    val planoId: String? = null,
    
    // Configurações do treino (campos do formulário)
    val caloriasPerdidas: Int? = null,
    val dificuldade: DificuldadeTreino? = null,
    val tipoTreino: TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null, // Escala de 1-10
    val observacoesTreino: String? = null
)

@Serializable
data class ConteudoUpdateRequest(
    val urlVideo: String? = null, // URL do vídeo após upload
    val textoMarkdown: String? = null, // Conteúdo HTML do rich text editor
    val arquivoUrl: String? = null // URL do arquivo complementar após upload
)


