package com.example.treine_me.models

import com.example.treine_me.enums.StatusInscricao
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.*

object Inscricoes : BaseTable("inscricoes") {
    val dtInicio = timestamp("dt_inicio")
    val dtFim = timestamp("dt_fim").nullable()
    val status = enumerationByName<StatusInscricao>("status", 20)
    val alunoId = reference("aluno_id", Alunos, onDelete = ReferenceOption.CASCADE)
    val planoId = reference("plano_id", Planos, onDelete = ReferenceOption.CASCADE)
}

class InscricaoEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<InscricaoEntity>(Inscricoes)
    
    var dtInicio by Inscricoes.dtInicio
    var dtFim by Inscricoes.dtFim
    var status by Inscricoes.status
    var aluno by AlunoEntity referencedOn Inscricoes.alunoId
    var plano by PlanoEntity referencedOn Inscricoes.planoId
    var dtIns by Inscricoes.dtIns
    var dtUpd by Inscricoes.dtUpd
    var idUserIns by Inscricoes.idUserIns
    var idUserUpd by Inscricoes.idUserUpd
    var isActive by Inscricoes.isActive
}

@Serializable
data class Inscricao(
    override val id: String,
    val dtInicio: Instant,
    val dtFim: Instant? = null,
    val status: StatusInscricao,
    val alunoId: String,
    val planoId: String,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

@Serializable
data class InscricaoCreateRequest(
    val alunoId: String,
    val planoId: String,
    val dtInicio: Instant,
    val dtFim: Instant? = null
)

@Serializable
data class InscricaoUpdateRequest(
    val status: StatusInscricao? = null,
    val dtFim: Instant? = null
)

@Serializable
data class InscricaoResponse(
    val id: String,
    val dtInicio: Instant,
    val dtFim: Instant? = null,
    val status: StatusInscricao,
    val aluno: com.example.treine_me.api.AlunoResponse,
    val plano: PlanoResponse
)
