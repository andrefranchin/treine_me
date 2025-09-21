package com.example.treine_me.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object Conteudos : BaseTable("conteudos") {
    val urlVideo = varchar("url_video", 500).nullable() // URL do vídeo hospedado
    val textoMarkdown = text("texto_markdown").nullable() // Conteúdo HTML do rich text editor
    val arquivoUrl = varchar("arquivo_url", 500).nullable() // URL do arquivo complementar
    val aulaId = reference("aula_id", Aulas, onDelete = ReferenceOption.CASCADE).uniqueIndex()
}

class ConteudoEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ConteudoEntity>(Conteudos)
    
    var urlVideo by Conteudos.urlVideo
    var textoMarkdown by Conteudos.textoMarkdown
    var arquivoUrl by Conteudos.arquivoUrl
    var aula by AulaEntity referencedOn Conteudos.aulaId
    var dtIns by Conteudos.dtIns
    var dtUpd by Conteudos.dtUpd
    var idUserIns by Conteudos.idUserIns
    var idUserUpd by Conteudos.idUserUpd
    var isActive by Conteudos.isActive
}

@Serializable
data class Conteudo(
    override val id: String,
    val urlVideo: String? = null,
    val textoMarkdown: String? = null,
    val arquivoUrl: String? = null,
    val aulaId: String,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

@Serializable
data class ConteudoCreateRequest(
    val urlVideo: String? = null, // URL do vídeo após upload
    val textoMarkdown: String? = null, // Conteúdo HTML do rich text editor
    val arquivoUrl: String? = null // URL do arquivo complementar após upload
)

@Serializable
data class ConteudoUpdateRequest(
    val urlVideo: String? = null, // URL do vídeo após upload
    val textoMarkdown: String? = null, // Conteúdo HTML do rich text editor
    val arquivoUrl: String? = null // URL do arquivo complementar após upload
)

@Serializable
data class ConteudoResponse(
    val id: String,
    val urlVideo: String? = null,
    val textoMarkdown: String? = null,
    val arquivoUrl: String? = null,
    val aulaId: String
)
