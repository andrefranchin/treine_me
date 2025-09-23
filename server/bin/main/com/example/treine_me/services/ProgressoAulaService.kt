package com.example.treine_me.services

import com.example.treine_me.exceptions.ForbiddenException
import com.example.treine_me.exceptions.NotFoundException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.*
import com.example.treine_me.enums.StatusInscricao
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Serviço para gerenciar o progresso dos alunos nas aulas.
 * Permite registrar e acompanhar o progresso de visualização de aulas.
 */
class ProgressoAulaService {
    
    // ========== REGISTRO DE PROGRESSO (ALUNO) ==========
    
    fun registrarProgresso(request: ProgressoAulaCreateRequest, alunoId: String): ProgressoAulaResponse {
        validateProgressoRequest(request)
        
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            val aula = AulaEntity.find { 
                (Aulas.id eq UUID.fromString(request.aulaId)) and (Aulas.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            
            val professor = aula.modulo.produto.professor
            
            // Verificar se o aluno tem acesso à aula (através do plano)
            val temAcesso = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and
                (Inscricoes.planoId eq aula.plano.id) and
                (Inscricoes.status eq StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true)
            }.firstOrNull() != null
            
            if (!temAcesso) {
                throw ForbiddenException("Você não tem acesso a esta aula")
            }
            
            val now = Clock.System.now()
            
            // Buscar progresso existente ou criar novo
            val progressoExistente = ProgressoAulaEntity.find {
                (ProgressosAula.alunoId eq aluno.id) and (ProgressosAula.aulaId eq aula.id)
            }.firstOrNull()
            
            val progresso = if (progressoExistente != null) {
                // Atualizar progresso existente
                progressoExistente.minutosTotaisAssistidos = maxOf(
                    progressoExistente.minutosTotaisAssistidos, 
                    request.minutosTotaisAssistidos
                )
                progressoExistente.ultimoMinutoAssistido = request.ultimoMinutoAssistido
                progressoExistente.percentualConcluido = request.percentualConcluido
                progressoExistente.concluida = request.concluida
                progressoExistente.dataUltimaVisualizacao = now
                progressoExistente.numeroVisualizacoes = progressoExistente.numeroVisualizacoes + 1
                
                if (request.concluida && progressoExistente.dataConclusao == null) {
                    progressoExistente.dataConclusao = now
                }
                
                request.dispositivoUltimaVisualizacao?.let { 
                    progressoExistente.dispositivoUltimaVisualizacao = it 
                }
                
                progressoExistente.dtUpd = now
                progressoExistente.idUserUpd = UUID.fromString(alunoId)
                
                progressoExistente
            } else {
                // Criar novo progresso
                ProgressoAulaEntity.new {
                    this.aluno = aluno
                    this.aula = aula
                    this.professor = professor
                    minutosTotaisAssistidos = request.minutosTotaisAssistidos
                    ultimoMinutoAssistido = request.ultimoMinutoAssistido
                    percentualConcluido = request.percentualConcluido
                    concluida = request.concluida
                    dataInicioVisualizacao = now
                    dataUltimaVisualizacao = now
                    dataConclusao = if (request.concluida) now else null
                    numeroVisualizacoes = 1
                    tempoTotalSessao = request.minutosTotaisAssistidos * 60
                    dispositivoUltimaVisualizacao = request.dispositivoUltimaVisualizacao
                    dtIns = now
                    dtUpd = now
                    idUserIns = UUID.fromString(alunoId)
                    idUserUpd = UUID.fromString(alunoId)
                    isActive = true
                }
            }
            
            ProgressoAulaResponse(
                id = progresso.id.value.toString(),
                alunoId = aluno.id.value.toString(),
                aulaId = aula.id.value.toString(),
                professorId = professor.id.value.toString(),
                minutosTotaisAssistidos = progresso.minutosTotaisAssistidos,
                ultimoMinutoAssistido = progresso.ultimoMinutoAssistido,
                percentualConcluido = progresso.percentualConcluido,
                concluida = progresso.concluida,
                dataInicioVisualizacao = progresso.dataInicioVisualizacao,
                dataUltimaVisualizacao = progresso.dataUltimaVisualizacao,
                dataConclusao = progresso.dataConclusao,
                numeroVisualizacoes = progresso.numeroVisualizacoes,
                tempoTotalSessao = progresso.tempoTotalSessao,
                dispositivoUltimaVisualizacao = progresso.dispositivoUltimaVisualizacao,
                aula = AulaProgressoInfo(
                    id = aula.id.value.toString(),
                    titulo = aula.titulo,
                    ordem = aula.ordem,
                    videoDuracaoSegundos = aula.videoDuracaoSegundos,
                    moduloTitulo = aula.modulo.titulo,
                    produtoTitulo = aula.modulo.produto.titulo
                )
            )
        }
    }
    
    fun getMeuProgresso(alunoId: String, page: Int = 1, size: Int = 20): PaginatedResponse<ProgressoAulaResponse> {
        return transaction {
            val offset = (page - 1) * size
            
            val progressos = ProgressoAulaEntity.find {
                (ProgressosAula.alunoId eq UUID.fromString(alunoId)) and (ProgressosAula.isActive eq true)
            }.orderBy(ProgressosAula.dataUltimaVisualizacao to org.jetbrains.exposed.sql.SortOrder.DESC)
                .drop(offset)
                .take(size)
                .map { progresso ->
                    ProgressoAulaResponse(
                        id = progresso.id.value.toString(),
                        alunoId = progresso.aluno.id.value.toString(),
                        aulaId = progresso.aula.id.value.toString(),
                        professorId = progresso.professor.id.value.toString(),
                        minutosTotaisAssistidos = progresso.minutosTotaisAssistidos,
                        ultimoMinutoAssistido = progresso.ultimoMinutoAssistido,
                        percentualConcluido = progresso.percentualConcluido,
                        concluida = progresso.concluida,
                        dataInicioVisualizacao = progresso.dataInicioVisualizacao,
                        dataUltimaVisualizacao = progresso.dataUltimaVisualizacao,
                        dataConclusao = progresso.dataConclusao,
                        numeroVisualizacoes = progresso.numeroVisualizacoes,
                        tempoTotalSessao = progresso.tempoTotalSessao,
                        dispositivoUltimaVisualizacao = progresso.dispositivoUltimaVisualizacao,
                        aula = AulaProgressoInfo(
                            id = progresso.aula.id.value.toString(),
                            titulo = progresso.aula.titulo,
                            ordem = progresso.aula.ordem,
                            videoDuracaoSegundos = progresso.aula.videoDuracaoSegundos,
                            moduloTitulo = progresso.aula.modulo.titulo,
                            produtoTitulo = progresso.aula.modulo.produto.titulo
                        )
                    )
                }
            
            val total = ProgressoAulaEntity.find { 
                (ProgressosAula.alunoId eq UUID.fromString(alunoId)) and (ProgressosAula.isActive eq true) 
            }.count()
            
            PaginatedResponse(
                data = progressos,
                page = page,
                size = size,
                total = total,
                totalPages = (total + size - 1) / size
            )
        }
    }
    
    fun getProgressoAula(aulaId: String, alunoId: String): ProgressoAulaResponse? {
        return transaction {
            val progresso = ProgressoAulaEntity.find {
                (ProgressosAula.alunoId eq UUID.fromString(alunoId)) and 
                (ProgressosAula.aulaId eq UUID.fromString(aulaId)) and 
                (ProgressosAula.isActive eq true)
            }.firstOrNull()
            
            progresso?.let {
                ProgressoAulaResponse(
                    id = it.id.value.toString(),
                    alunoId = it.aluno.id.value.toString(),
                    aulaId = it.aula.id.value.toString(),
                    professorId = it.professor.id.value.toString(),
                    minutosTotaisAssistidos = it.minutosTotaisAssistidos,
                    ultimoMinutoAssistido = it.ultimoMinutoAssistido,
                    percentualConcluido = it.percentualConcluido,
                    concluida = it.concluida,
                    dataInicioVisualizacao = it.dataInicioVisualizacao,
                    dataUltimaVisualizacao = it.dataUltimaVisualizacao,
                    dataConclusao = it.dataConclusao,
                    numeroVisualizacoes = it.numeroVisualizacoes,
                    tempoTotalSessao = it.tempoTotalSessao,
                    dispositivoUltimaVisualizacao = it.dispositivoUltimaVisualizacao,
                    aula = AulaProgressoInfo(
                        id = it.aula.id.value.toString(),
                        titulo = it.aula.titulo,
                        ordem = it.aula.ordem,
                        videoDuracaoSegundos = it.aula.videoDuracaoSegundos,
                        moduloTitulo = it.aula.modulo.titulo,
                        produtoTitulo = it.aula.modulo.produto.titulo
                    )
                )
            }
        }
    }
    
    // ========== VISUALIZAÇÃO DE PROGRESSO (PROFESSOR) ==========
    
    fun getProgressoAluno(alunoId: String, professorId: String): ResumoProgressoAluno {
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            // Verificar se o professor tem permissão para ver este aluno
            val temPermissao = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and (Inscricoes.isActive eq true)
            }.any { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
            
            if (!temPermissao) {
                throw ForbiddenException("Você não tem permissão para ver o progresso deste aluno")
            }
            
            val progressos = ProgressoAulaEntity.find {
                (ProgressosAula.alunoId eq aluno.id) and 
                (ProgressosAula.professorId eq UUID.fromString(professorId)) and
                (ProgressosAula.isActive eq true)
            }
            
            val totalAulas = progressos.count().toInt()
            val aulasAssistidas = progressos.count { it.minutosTotaisAssistidos > 0 }
            val aulasConcluidas = progressos.count { it.concluida }
            val minutosTotais = progressos.sumOf { it.minutosTotaisAssistidos }
            val ultimaAtividade = progressos.maxByOrNull { it.dataUltimaVisualizacao ?: it.dtIns }?.dataUltimaVisualizacao
            
            val percentualGeral = if (totalAulas > 0) {
                (aulasConcluidas * 100) / totalAulas
            } else 0
            
            ResumoProgressoAluno(
                alunoId = alunoId,
                professorId = professorId,
                totalAulas = totalAulas,
                aulasAssistidas = aulasAssistidas,
                aulasConcluidas = aulasConcluidas,
                minutosTotaisAssistidos = minutosTotais,
                percentualGeralConcluido = percentualGeral,
                ultimaAtividade = ultimaAtividade
            )
        }
    }
    
    fun getProgressoPorModulo(alunoId: String, professorId: String): List<ProgressoPorModulo> {
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            // Verificar permissão
            val temPermissao = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and (Inscricoes.isActive eq true)
            }.any { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
            
            if (!temPermissao) {
                throw ForbiddenException("Você não tem permissão para ver o progresso deste aluno")
            }
            
            // Buscar todos os progressos do aluno com este professor
            val progressos = ProgressoAulaEntity.find {
                (ProgressosAula.alunoId eq aluno.id) and 
                (ProgressosAula.professorId eq UUID.fromString(professorId)) and
                (ProgressosAula.isActive eq true)
            }
            
            // Agrupar por módulo
            val progressosPorModulo = progressos.groupBy { it.aula.modulo.id.value }
            
            progressosPorModulo.map { (moduloId, progressosModulo) ->
                val modulo = progressosModulo.first().aula.modulo
                val totalAulas = progressosModulo.size
                val aulasAssistidas = progressosModulo.count { it.minutosTotaisAssistidos > 0 }
                val aulasConcluidas = progressosModulo.count { it.concluida }
                val percentualConcluido = if (totalAulas > 0) {
                    (aulasConcluidas * 100) / totalAulas
                } else 0
                
                val aulasProgresso = progressosModulo
                    .sortedBy { it.aula.ordem }
                    .map { progresso ->
                        ProgressoAulaResponse(
                            id = progresso.id.value.toString(),
                            alunoId = progresso.aluno.id.value.toString(),
                            aulaId = progresso.aula.id.value.toString(),
                            professorId = progresso.professor.id.value.toString(),
                            minutosTotaisAssistidos = progresso.minutosTotaisAssistidos,
                            ultimoMinutoAssistido = progresso.ultimoMinutoAssistido,
                            percentualConcluido = progresso.percentualConcluido,
                            concluida = progresso.concluida,
                            dataInicioVisualizacao = progresso.dataInicioVisualizacao,
                            dataUltimaVisualizacao = progresso.dataUltimaVisualizacao,
                            dataConclusao = progresso.dataConclusao,
                            numeroVisualizacoes = progresso.numeroVisualizacoes,
                            tempoTotalSessao = progresso.tempoTotalSessao,
                            dispositivoUltimaVisualizacao = progresso.dispositivoUltimaVisualizacao,
                            aula = AulaProgressoInfo(
                                id = progresso.aula.id.value.toString(),
                                titulo = progresso.aula.titulo,
                                ordem = progresso.aula.ordem,
                                videoDuracaoSegundos = progresso.aula.videoDuracaoSegundos,
                                moduloTitulo = progresso.aula.modulo.titulo,
                                produtoTitulo = progresso.aula.modulo.produto.titulo
                            )
                        )
                    }
                
                ProgressoPorModulo(
                    moduloId = moduloId.toString(),
                    moduloTitulo = modulo.titulo,
                    totalAulas = totalAulas,
                    aulasAssistidas = aulasAssistidas,
                    aulasConcluidas = aulasConcluidas,
                    percentualConcluido = percentualConcluido,
                    aulas = aulasProgresso
                )
            }.sortedBy { it.moduloTitulo }
        }
    }
    
    fun getProgressoDetalhado(alunoId: String, professorId: String, page: Int = 1, size: Int = 20): PaginatedResponse<ProgressoAulaResponse> {
        return transaction {
            // Verificar permissão
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            val temPermissao = InscricaoEntity.find {
                (Inscricoes.alunoId eq aluno.id) and (Inscricoes.isActive eq true)
            }.any { inscricao ->
                inscricao.plano.professor.id.value.toString() == professorId
            }
            
            if (!temPermissao) {
                throw ForbiddenException("Você não tem permissão para ver o progresso deste aluno")
            }
            
            val offset = (page - 1) * size
            
            val progressos = ProgressoAulaEntity.find {
                (ProgressosAula.alunoId eq UUID.fromString(alunoId)) and 
                (ProgressosAula.professorId eq UUID.fromString(professorId)) and
                (ProgressosAula.isActive eq true)
            }.orderBy(ProgressosAula.dataUltimaVisualizacao to org.jetbrains.exposed.sql.SortOrder.DESC)
                .drop(offset)
                .take(size)
                .map { progresso ->
                    ProgressoAulaResponse(
                        id = progresso.id.value.toString(),
                        alunoId = progresso.aluno.id.value.toString(),
                        aulaId = progresso.aula.id.value.toString(),
                        professorId = progresso.professor.id.value.toString(),
                        minutosTotaisAssistidos = progresso.minutosTotaisAssistidos,
                        ultimoMinutoAssistido = progresso.ultimoMinutoAssistido,
                        percentualConcluido = progresso.percentualConcluido,
                        concluida = progresso.concluida,
                        dataInicioVisualizacao = progresso.dataInicioVisualizacao,
                        dataUltimaVisualizacao = progresso.dataUltimaVisualizacao,
                        dataConclusao = progresso.dataConclusao,
                        numeroVisualizacoes = progresso.numeroVisualizacoes,
                        tempoTotalSessao = progresso.tempoTotalSessao,
                        dispositivoUltimaVisualizacao = progresso.dispositivoUltimaVisualizacao,
                        aula = AulaProgressoInfo(
                            id = progresso.aula.id.value.toString(),
                            titulo = progresso.aula.titulo,
                            ordem = progresso.aula.ordem,
                            videoDuracaoSegundos = progresso.aula.videoDuracaoSegundos,
                            moduloTitulo = progresso.aula.modulo.titulo,
                            produtoTitulo = progresso.aula.modulo.produto.titulo
                        )
                    )
                }
            
            val total = ProgressoAulaEntity.find { 
                (ProgressosAula.alunoId eq UUID.fromString(alunoId)) and 
                (ProgressosAula.professorId eq UUID.fromString(professorId)) and
                (ProgressosAula.isActive eq true) 
            }.count()
            
            PaginatedResponse(
                data = progressos,
                page = page,
                size = size,
                total = total,
                totalPages = (total + size - 1) / size
            )
        }
    }
    
    // ========== UTILITÁRIOS ==========
    
    private fun validateProgressoRequest(request: ProgressoAulaCreateRequest) {
        if (request.aulaId.isBlank()) {
            throw ValidationException("ID da aula é obrigatório", "aulaId")
        }
        if (request.minutosTotaisAssistidos < 0) {
            throw ValidationException("Minutos assistidos não pode ser negativo", "minutosTotaisAssistidos")
        }
        if (request.ultimoMinutoAssistido < 0) {
            throw ValidationException("Último minuto assistido não pode ser negativo", "ultimoMinutoAssistido")
        }
        if (request.percentualConcluido < 0 || request.percentualConcluido > 100) {
            throw ValidationException("Percentual concluído deve estar entre 0 e 100", "percentualConcluido")
        }
    }
}
