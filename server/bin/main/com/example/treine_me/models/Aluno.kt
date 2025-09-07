package com.example.treine_me.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

object Alunos : BaseTable("alunos") {
    val nome = varchar("nome", 255)
    val email = varchar("email", 255).uniqueIndex()
    val senhaHash = varchar("senha_hash", 255)
    val fotoPerfilUrl = varchar("foto_perfil_url", 500).nullable()
}

class AlunoEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AlunoEntity>(Alunos)
    
    var nome by Alunos.nome
    var email by Alunos.email
    var senhaHash by Alunos.senhaHash
    var fotoPerfilUrl by Alunos.fotoPerfilUrl
    var dtIns by Alunos.dtIns
    var dtUpd by Alunos.dtUpd
    var idUserIns by Alunos.idUserIns
    var idUserUpd by Alunos.idUserUpd
    var isActive by Alunos.isActive
}

@Serializable
data class Aluno(
    override val id: String,
    val nome: String,
    val email: String,
    val fotoPerfilUrl: String? = null,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

// Use shared DTOs to avoid duplication
typealias AlunoCreateRequest = com.example.treine_me.api.AlunoCreateRequest

@Serializable
data class AlunoUpdateRequest(
    val nome: String? = null,
    val fotoPerfilUrl: String? = null
)

typealias AlunoResponse = com.example.treine_me.api.AlunoResponse
