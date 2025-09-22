package com.example.treine_me.models

import com.example.treine_me.enums.TipoConteudo
import com.example.treine_me.enums.DificuldadeTreino
import com.example.treine_me.enums.TipoTreino
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
    val planoId = reference("plano_id", Planos, onDelete = ReferenceOption.RESTRICT)
    val moduloId = reference("modulo_id", Modulos, onDelete = ReferenceOption.CASCADE)
    
    // Metadados do vídeo (preenchidos automaticamente)
    val videoDuracaoSegundos = integer("video_duracao_segundos").nullable()
    val videoResolucao = varchar("video_resolucao", 20).nullable() // Ex: "1080p", "720p", "480p"
    val videoTamanhoBytes = long("video_tamanho_bytes").nullable()
    val videoCodec = varchar("video_codec", 50).nullable() // Ex: "H.264", "H.265", "VP9"
    val videoFps = integer("video_fps").nullable() // Frames per second
    val videoAspectRatio = varchar("video_aspect_ratio", 20).nullable() // Ex: "16:9", "4:3"
    
    // Configurações do treino (preenchidas pelo professor)
    val caloriasPerdidas = integer("calorias_perdidas").nullable() // Estimativa de calorias queimadas
    val dificuldade = enumerationByName<DificuldadeTreino>("dificuldade", 20).nullable()
    val tipoTreino = enumerationByName<TipoTreino>("tipo_treino", 20).nullable()
    val equipamentosNecessarios = text("equipamentos_necessarios").nullable() // Lista de equipamentos
    val duracaoTreinoMinutos = integer("duracao_treino_minutos").nullable() // Duração efetiva do treino
    val intensidade = integer("intensidade").nullable() // Escala de 1-10
    val observacoesTreino = text("observacoes_treino").nullable() // Observações especiais
}

class AulaEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AulaEntity>(Aulas)
    
    var titulo by Aulas.titulo
    var descricao by Aulas.descricao
    var ordem by Aulas.ordem
    var tipoConteudo by Aulas.tipoConteudo
    var plano by PlanoEntity referencedOn Aulas.planoId
    var modulo by ModuloEntity referencedOn Aulas.moduloId
    
    // Metadados do vídeo
    var videoDuracaoSegundos by Aulas.videoDuracaoSegundos
    var videoResolucao by Aulas.videoResolucao
    var videoTamanhoBytes by Aulas.videoTamanhoBytes
    var videoCodec by Aulas.videoCodec
    var videoFps by Aulas.videoFps
    var videoAspectRatio by Aulas.videoAspectRatio
    
    // Configurações do treino
    var caloriasPerdidas by Aulas.caloriasPerdidas
    var dificuldade by Aulas.dificuldade
    var tipoTreino by Aulas.tipoTreino
    var equipamentosNecessarios by Aulas.equipamentosNecessarios
    var duracaoTreinoMinutos by Aulas.duracaoTreinoMinutos
    var intensidade by Aulas.intensidade
    var observacoesTreino by Aulas.observacoesTreino
    
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
    val planoId: String,
    val moduloId: String,
    
    // Metadados do vídeo
    val videoDuracaoSegundos: Int? = null,
    val videoResolucao: String? = null,
    val videoTamanhoBytes: Long? = null,
    val videoCodec: String? = null,
    val videoFps: Int? = null,
    val videoAspectRatio: String? = null,
    
    // Configurações do treino
    val caloriasPerdidas: Int? = null,
    val dificuldade: DificuldadeTreino? = null,
    val tipoTreino: TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null,
    val observacoesTreino: String? = null,
    
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
    val ordem: Int? = null, // Opcional - será calculado automaticamente se não fornecido
    val tipoConteudo: TipoConteudo,
    val planoId: String,
    
    // Configurações do treino (campos do formulário)
    val caloriasPerdidas: Int? = null,
    val dificuldade: DificuldadeTreino? = null,
    val tipoTreino: TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null, // Escala de 1-10
    val observacoesTreino: String? = null
    
    // Nota: Metadados do vídeo não estão aqui pois são preenchidos automaticamente
)

@Serializable
data class AulaUpdateRequest(
    val titulo: String? = null,
    val descricao: String? = null,
    val ordem: Int? = null,
    val tipoConteudo: TipoConteudo? = null,
    val planoId: String? = null,
    
    // Configurações do treino (campos do formulário)
    val caloriasPerdidas: Int? = null,
    val dificuldade: DificuldadeTreino? = null,
    val tipoTreino: TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null, // Escala de 1-10
    val observacoesTreino: String? = null
    
    // Nota: Metadados do vídeo não estão aqui pois são preenchidos automaticamente
)

@Serializable
data class AulaResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: TipoConteudo,
    val planoId: String,
    val moduloId: String,
    val conteudo: ConteudoResponse? = null,
    
    // Metadados do vídeo
    val videoDuracaoSegundos: Int? = null,
    val videoResolucao: String? = null,
    val videoTamanhoBytes: Long? = null,
    val videoCodec: String? = null,
    val videoFps: Int? = null,
    val videoAspectRatio: String? = null,
    
    // Configurações do treino
    val caloriasPerdidas: Int? = null,
    val dificuldade: DificuldadeTreino? = null,
    val tipoTreino: TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null,
    val observacoesTreino: String? = null
)

@Serializable
data class ReorderAulasRequest(
    val aulaIds: List<String>
)
