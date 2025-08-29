package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
	ADMIN, PROFESSOR, ALUNO
}

@Serializable
enum class Recorrencia {
	MENSAL, ANUAL
}

@Serializable
enum class StatusInscricao {
	ATIVA, CANCELADA, PENDENTE, EXPIRADA
}

@Serializable
enum class TipoProduto {
	CURSO, MENTORIA, EBOOK
}

@Serializable
enum class TipoConteudo {
	VIDEO, TEXTO, ATIVIDADE
}


