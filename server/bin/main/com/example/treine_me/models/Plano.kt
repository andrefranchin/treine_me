package com.example.treine_me.models

import com.example.treine_me.enums.Recorrencia
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import java.math.BigDecimal
import java.util.*

object Planos : BaseTable("planos") {
    val nome = varchar("nome", 255)
    val descricao = text("descricao")
    val valor = decimal("valor", 10, 2)
    val recorrencia = enumerationByName<Recorrencia>("recorrencia", 20)
    val professorId = reference("professor_id", Professores, onDelete = ReferenceOption.CASCADE)
}

class PlanoEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlanoEntity>(Planos)
    
    var nome by Planos.nome
    var descricao by Planos.descricao
    var valor by Planos.valor
    var recorrencia by Planos.recorrencia
    var professor by ProfessorEntity referencedOn Planos.professorId
    var dtIns by Planos.dtIns
    var dtUpd by Planos.dtUpd
    var idUserIns by Planos.idUserIns
    var idUserUpd by Planos.idUserUpd
    var isActive by Planos.isActive
}

@Serializable
data class Plano(
    override val id: String,
    val nome: String,
    val descricao: String,
    val valor: String, // Usando String para evitar problemas de serialização com BigDecimal
    val recorrencia: Recorrencia,
    val professorId: String,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

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
