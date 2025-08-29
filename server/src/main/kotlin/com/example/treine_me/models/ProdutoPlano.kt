package com.example.treine_me.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object ProdutoPlanos : BaseTable("produto_planos") {
    val produtoId = reference("produto_id", Produtos, onDelete = ReferenceOption.CASCADE)
    val planoId = reference("plano_id", Planos, onDelete = ReferenceOption.CASCADE)
    
    init {
        uniqueIndex(produtoId, planoId)
    }
}

class ProdutoPlanoEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ProdutoPlanoEntity>(ProdutoPlanos)
    
    var produto by ProdutoEntity referencedOn ProdutoPlanos.produtoId
    var plano by PlanoEntity referencedOn ProdutoPlanos.planoId
    var dtIns by ProdutoPlanos.dtIns
    var dtUpd by ProdutoPlanos.dtUpd
    var idUserIns by ProdutoPlanos.idUserIns
    var idUserUpd by ProdutoPlanos.idUserUpd
    var isActive by ProdutoPlanos.isActive
}

@Serializable
data class ProdutoPlano(
    override val id: String,
    val produtoId: String,
    val planoId: String,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

@Serializable
data class ProdutoPlanoCreateRequest(
    val produtoId: String,
    val planoId: String
)
