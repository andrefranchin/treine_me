package com.example.treine_me.models

import com.example.treine_me.enums.UserRole
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

object Professores : BaseTable("professores") {
    val nome = varchar("nome", 255)
    val email = varchar("email", 255).uniqueIndex()
    val senhaHash = varchar("senha_hash", 255)
    val bio = text("bio").nullable()
    val fotoPerfilUrl = varchar("foto_perfil_url", 500).nullable()
}

class ProfessorEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ProfessorEntity>(Professores)
    
    var nome by Professores.nome
    var email by Professores.email
    var senhaHash by Professores.senhaHash
    var bio by Professores.bio
    var fotoPerfilUrl by Professores.fotoPerfilUrl
    var dtIns by Professores.dtIns
    var dtUpd by Professores.dtUpd
    var idUserIns by Professores.idUserIns
    var idUserUpd by Professores.idUserUpd
    var isActive by Professores.isActive
}

@Serializable
data class Professor(
    override val id: String,
    val nome: String,
    val email: String,
    val bio: String? = null,
    val fotoPerfilUrl: String? = null,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

@Serializable
data class ProfessorCreateRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val bio: String? = null,
    val fotoPerfilUrl: String? = null
)

@Serializable
data class ProfessorUpdateRequest(
    val nome: String? = null,
    val bio: String? = null,
    val fotoPerfilUrl: String? = null
)

@Serializable
data class ProfessorResponse(
    val id: String,
    val nome: String,
    val email: String,
    val bio: String? = null,
    val fotoPerfilUrl: String? = null,
    val role: UserRole = UserRole.PROFESSOR
)
