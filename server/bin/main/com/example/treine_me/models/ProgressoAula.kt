package com.example.treine_me.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.*

object ProgressosAula : BaseTable("progressos_aula") {
    val alunoId = reference("aluno_id", Alunos, onDelete = ReferenceOption.CASCADE)
    val aulaId = reference("aula_id", Aulas, onDelete = ReferenceOption.CASCADE)
    val professorId = reference("professor_id", Professores, onDelete = ReferenceOption.CASCADE) // Para filtrar por professor
    
    // Dados de progresso
    val minutosTotaisAssistidos = integer("minutos_totais_assistidos").default(0)
    val ultimoMinutoAssistido = integer("ultimo_minuto_assistido").default(0) // Para continuar de onde parou
    val percentualConcluido = integer("percentual_concluido").default(0) // 0-100
    val concluida = bool("concluida").default(false)
    val dataInicioVisualizacao = timestamp("data_inicio_visualizacao").nullable()
    val dataUltimaVisualizacao = timestamp("data_ultima_visualizacao").nullable()
    val dataConclusao = timestamp("data_conclusao").nullable()
    
    // Metadados adicionais
    val numeroVisualizacoes = integer("numero_visualizacoes").default(0)
    val tempoTotalSessao = integer("tempo_total_sessao").default(0) // Em segundos
    val dispositivoUltimaVisualizacao = varchar("dispositivo_ultima_visualizacao", 50).nullable()
    
    init {
        // Índice único para garantir um registro por aluno/aula
        uniqueIndex(alunoId, aulaId)
        // Índice para buscar por professor
        index(false, professorId)
    }
}

class ProgressoAulaEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ProgressoAulaEntity>(ProgressosAula)
    
    var aluno by AlunoEntity referencedOn ProgressosAula.alunoId
    var aula by AulaEntity referencedOn ProgressosAula.aulaId
    var professor by ProfessorEntity referencedOn ProgressosAula.professorId
    
    var minutosTotaisAssistidos by ProgressosAula.minutosTotaisAssistidos
    var ultimoMinutoAssistido by ProgressosAula.ultimoMinutoAssistido
    var percentualConcluido by ProgressosAula.percentualConcluido
    var concluida by ProgressosAula.concluida
    var dataInicioVisualizacao by ProgressosAula.dataInicioVisualizacao
    var dataUltimaVisualizacao by ProgressosAula.dataUltimaVisualizacao
    var dataConclusao by ProgressosAula.dataConclusao
    
    var numeroVisualizacoes by ProgressosAula.numeroVisualizacoes
    var tempoTotalSessao by ProgressosAula.tempoTotalSessao
    var dispositivoUltimaVisualizacao by ProgressosAula.dispositivoUltimaVisualizacao
    
    var dtIns by ProgressosAula.dtIns
    var dtUpd by ProgressosAula.dtUpd
    var idUserIns by ProgressosAula.idUserIns
    var idUserUpd by ProgressosAula.idUserUpd
    var isActive by ProgressosAula.isActive
}

@Serializable
data class ProgressoAula(
    override val id: String,
    val alunoId: String,
    val aulaId: String,
    val professorId: String,
    val minutosTotaisAssistidos: Int,
    val ultimoMinutoAssistido: Int,
    val percentualConcluido: Int,
    val concluida: Boolean,
    val dataInicioVisualizacao: Instant?,
    val dataUltimaVisualizacao: Instant?,
    val dataConclusao: Instant?,
    val numeroVisualizacoes: Int,
    val tempoTotalSessao: Int,
    val dispositivoUltimaVisualizacao: String?,
    override val dtIns: Instant,
    override val dtUpd: Instant,
    override val idUserIns: String,
    override val idUserUpd: String,
    override val isActive: Boolean
) : BaseEntity()

@Serializable
data class ProgressoAulaCreateRequest(
    val aulaId: String,
    val minutosTotaisAssistidos: Int,
    val ultimoMinutoAssistido: Int,
    val percentualConcluido: Int,
    val concluida: Boolean = false,
    val dispositivoUltimaVisualizacao: String? = null
)

@Serializable
data class ProgressoAulaUpdateRequest(
    val minutosTotaisAssistidos: Int? = null,
    val ultimoMinutoAssistido: Int? = null,
    val percentualConcluido: Int? = null,
    val concluida: Boolean? = null,
    val dispositivoUltimaVisualizacao: String? = null
)

@Serializable
data class ProgressoAulaResponse(
    val id: String,
    val alunoId: String,
    val aulaId: String,
    val professorId: String,
    val minutosTotaisAssistidos: Int,
    val ultimoMinutoAssistido: Int,
    val percentualConcluido: Int,
    val concluida: Boolean,
    val dataInicioVisualizacao: Instant?,
    val dataUltimaVisualizacao: Instant?,
    val dataConclusao: Instant?,
    val numeroVisualizacoes: Int,
    val tempoTotalSessao: Int,
    val dispositivoUltimaVisualizacao: String?,
    // Dados da aula para facilitar exibição
    val aula: AulaProgressoInfo? = null
)

@Serializable
data class AulaProgressoInfo(
    val id: String,
    val titulo: String,
    val ordem: Int,
    val videoDuracaoSegundos: Int?,
    val moduloTitulo: String,
    val produtoTitulo: String
)

@Serializable
data class ResumoProgressoAluno(
    val alunoId: String,
    val professorId: String,
    val totalAulas: Int,
    val aulasAssistidas: Int,
    val aulasConcluidas: Int,
    val minutosTotaisAssistidos: Int,
    val percentualGeralConcluido: Int,
    val ultimaAtividade: Instant?
)

@Serializable
data class ProgressoPorModulo(
    val moduloId: String,
    val moduloTitulo: String,
    val totalAulas: Int,
    val aulasAssistidas: Int,
    val aulasConcluidas: Int,
    val percentualConcluido: Int,
    val aulas: List<ProgressoAulaResponse>
)
