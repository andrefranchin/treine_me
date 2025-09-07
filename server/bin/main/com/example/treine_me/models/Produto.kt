package com.example.treine_me.models

import com.example.treine_me.enums.TipoProduto
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object Produtos : BaseTable("produtos") {
    val titulo = varchar("titulo", 255)
    val descricao = text("descricao")
    val tipo = enumerationByName<TipoProduto>("tipo", 20)
    val capaUrl = varchar("capa_url", 500).nullable()
    val videoIntroUrl = varchar("video_intro_url", 500).nullable()
    val professorId = reference("professor_id", Professores, onDelete = ReferenceOption.CASCADE)
}

class ProdutoEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ProdutoEntity>(Produtos)
    
    var titulo by Produtos.titulo
    var descricao by Produtos.descricao
    var tipo by Produtos.tipo
    var capaUrl by Produtos.capaUrl
    var videoIntroUrl by Produtos.videoIntroUrl
    var professor by ProfessorEntity referencedOn Produtos.professorId
    var dtIns by Produtos.dtIns
    var dtUpd by Produtos.dtUpd
    var idUserIns by Produtos.idUserIns
    var idUserUpd by Produtos.idUserUpd
    var isActive by Produtos.isActive
}

@Serializable
data class Produto(
    override val id: String,
    val titulo: String,
    val descricao: String,
    val tipo: TipoProduto,
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null,
    val professorId: String,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

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
