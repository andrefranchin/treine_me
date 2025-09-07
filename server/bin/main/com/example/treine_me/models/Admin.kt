package com.example.treine_me.models

import com.example.treine_me.enums.UserRole
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

object Admins : BaseTable("admins") {
    val nome = varchar("nome", 255)
    val email = varchar("email", 255).uniqueIndex()
    val senhaHash = varchar("senha_hash", 255)
}

class AdminEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AdminEntity>(Admins)
    
    var nome by Admins.nome
    var email by Admins.email
    var senhaHash by Admins.senhaHash
    var dtIns by Admins.dtIns
    var dtUpd by Admins.dtUpd
    var idUserIns by Admins.idUserIns
    var idUserUpd by Admins.idUserUpd
    var isActive by Admins.isActive
}

@Serializable
data class Admin(
    override val id: String,
    val nome: String,
    val email: String,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

@Serializable
data class AdminCreateRequest(
    val nome: String,
    val email: String,
    val senha: String
)

@Serializable
data class AdminResponse(
    val id: String,
    val nome: String,
    val email: String,
    val role: UserRole = UserRole.ADMIN
)
