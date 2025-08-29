package com.example.treine_me.models

import com.example.treine_me.enums.TipoConteudo
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object Aulas : BaseTable("aulas") {
    val titulo = varchar("titulo", 255)
    val descricao = text("descricao")
    val ordem = integer("ordem")
    val tipoConteudo = enumerationByName<TipoConteudo>("tipo_conteudo", 20)
    val moduloId = reference("modulo_id", Modulos, onDelete = ReferenceOption.CASCADE)
}

class AulaEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AulaEntity>(Aulas)
    
    var titulo by Aulas.titulo
    var descricao by Aulas.descricao
    var ordem by Aulas.ordem
    var tipoConteudo by Aulas.tipoConteudo
    var modulo by ModuloEntity referencedOn Aulas.moduloId
    var dtIns by Aulas.dtIns
    var dtUpd by Aulas.dtUpd
    var idUserIns by Aulas.idUserIns
    var idUserUpd by Aulas.idUserUpd
    var isActive by Aulas.isActive
}

@Serializable
data class Aula(
    override val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: TipoConteudo,
    val moduloId: String,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

@Serializable
data class AulaCreateRequest(
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: TipoConteudo
)

@Serializable
data class AulaUpdateRequest(
    val titulo: String? = null,
    val descricao: String? = null,
    val ordem: Int? = null,
    val tipoConteudo: TipoConteudo? = null
)

@Serializable
data class AulaResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: TipoConteudo,
    val moduloId: String,
    val conteudo: ConteudoResponse? = null
)

@Serializable
data class ReorderAulasRequest(
    val aulaIds: List<String>
)
