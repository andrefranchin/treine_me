package com.example.treine_me.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object Modulos : BaseTable("modulos") {
    val titulo = varchar("titulo", 255)
    val descricao = text("descricao")
    val ordem = integer("ordem")
    val capaUrl = varchar("capa_url", 500).nullable()
    val videoIntroUrl = varchar("video_intro_url", 500).nullable()
    val produtoId = reference("produto_id", Produtos, onDelete = ReferenceOption.CASCADE)
}

class ModuloEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ModuloEntity>(Modulos)
    
    var titulo by Modulos.titulo
    var descricao by Modulos.descricao
    var ordem by Modulos.ordem
    var capaUrl by Modulos.capaUrl
    var videoIntroUrl by Modulos.videoIntroUrl
    var produto by ProdutoEntity referencedOn Modulos.produtoId
    var dtIns by Modulos.dtIns
    var dtUpd by Modulos.dtUpd
    var idUserIns by Modulos.idUserIns
    var idUserUpd by Modulos.idUserUpd
    var isActive by Modulos.isActive
}

@Serializable
data class Modulo(
    override val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null,
    val produtoId: String,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

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
data class ReorderModulosRequest(
    val moduloIds: List<String>
)
