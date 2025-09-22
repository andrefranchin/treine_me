package com.example.treine_me.services

import com.example.treine_me.exceptions.ForbiddenException
import com.example.treine_me.exceptions.NotFoundException
import com.example.treine_me.models.*
import com.example.treine_me.enums.StatusInscricao
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Serviço específico para funcionalidades de alunos autenticados.
 * Inclui verificação de inscrições e acesso a conteúdo.
 */
class AlunoService {
    
    // ========== INSCRIÇÕES ==========
    
    fun getMinhasInscricoes(alunoId: String, page: Int = 1, size: Int = 20): PaginatedResponse<InscricaoResponse> {
        return transaction {
            val offset = (page - 1) * size
            
            val inscricoes = InscricaoEntity.find {
                (Inscricoes.alunoId eq UUID.fromString(alunoId)) and (Inscricoes.isActive eq true)
            }.drop(offset)
                .take(size)
                .map { inscricao ->
                    InscricaoResponse(
                        id = inscricao.id.value.toString(),
                        dtInicio = inscricao.dtInicio,
                        dtFim = inscricao.dtFim,
                        status = inscricao.status,
                        aluno = AlunoResponse(
                            id = inscricao.aluno.id.value.toString(),
                            nome = inscricao.aluno.nome,
                            email = inscricao.aluno.email,
                            fotoPerfilUrl = inscricao.aluno.fotoPerfilUrl
                        ),
                        plano = PlanoResponse(
                            id = inscricao.plano.id.value.toString(),
                            nome = inscricao.plano.nome,
                            descricao = inscricao.plano.descricao,
                            valor = inscricao.plano.valor.toString(),
                            recorrencia = inscricao.plano.recorrencia,
                            professorId = inscricao.plano.professor.id.value.toString(),
                            professorNome = inscricao.plano.professor.nome
                        )
                    )
                }
            
            val total = InscricaoEntity.find { 
                (Inscricoes.alunoId eq UUID.fromString(alunoId)) and (Inscricoes.isActive eq true) 
            }.count()
            
            PaginatedResponse(
                data = inscricoes,
                page = page,
                size = size,
                total = total,
                totalPages = (total + size - 1) / size
            )
        }
    }
    
    fun getInscricao(inscricaoId: String, alunoId: String): InscricaoResponse {
        return transaction {
            val inscricao = InscricaoEntity.find { 
                (Inscricoes.id eq UUID.fromString(inscricaoId)) and 
                (Inscricoes.alunoId eq UUID.fromString(alunoId)) and 
                (Inscricoes.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Inscrição não encontrada")
            
            InscricaoResponse(
                id = inscricao.id.value.toString(),
                dtInicio = inscricao.dtInicio,
                dtFim = inscricao.dtFim,
                status = inscricao.status,
                aluno = AlunoResponse(
                    id = inscricao.aluno.id.value.toString(),
                    nome = inscricao.aluno.nome,
                    email = inscricao.aluno.email,
                    fotoPerfilUrl = inscricao.aluno.fotoPerfilUrl
                ),
                plano = PlanoResponse(
                    id = inscricao.plano.id.value.toString(),
                    nome = inscricao.plano.nome,
                    descricao = inscricao.plano.descricao,
                    valor = inscricao.plano.valor.toString(),
                    recorrencia = inscricao.plano.recorrencia,
                    professorId = inscricao.plano.professor.id.value.toString(),
                    professorNome = inscricao.plano.professor.nome
                )
            )
        }
    }
    
    // ========== ACESSO A CONTEÚDO ==========
    
    fun getConteudoAula(aulaId: String, alunoId: String, professorId: String): ConteudoResponse {
        return transaction {
            val aula = AulaEntity.find { 
                (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            
            // Verificar se a aula pertence ao professor
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw NotFoundException("Aula não encontrada")
            }
            
            // Verificar se o aluno tem acesso ao plano da aula
            val temAcesso = hasAccessToPlano(alunoId, aula.plano.id.value.toString())
            if (!temAcesso) {
                throw ForbiddenException("Você não tem acesso ao conteúdo desta aula. É necessário ter uma inscrição ativa no plano.")
            }
            
            val conteudo = ConteudoEntity.find { 
                (Conteudos.aulaId eq aula.id) and (Conteudos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Conteúdo da aula não encontrado")
            
            ConteudoResponse(
                id = conteudo.id.value.toString(),
                urlVideo = conteudo.urlVideo,
                textoMarkdown = conteudo.textoMarkdown,
                arquivoUrl = conteudo.arquivoUrl,
                aulaId = aula.id.value.toString()
            )
        }
    }
    
    fun getAulaCompleta(aulaId: String, alunoId: String, professorId: String): AulaCompletaResponse {
        return transaction {
            val aula = AulaEntity.find { 
                (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            
            // Verificar se a aula pertence ao professor
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw NotFoundException("Aula não encontrada")
            }
            
            // Verificar se o aluno tem acesso ao plano da aula
            val temAcesso = hasAccessToPlano(alunoId, aula.plano.id.value.toString())
            
            val conteudo = if (temAcesso) {
                ConteudoEntity.find { 
                    (Conteudos.aulaId eq aula.id) and (Conteudos.isActive eq true) 
                }.firstOrNull()?.let { conteudo ->
                    ConteudoResponse(
                        id = conteudo.id.value.toString(),
                        urlVideo = conteudo.urlVideo,
                        textoMarkdown = conteudo.textoMarkdown,
                        arquivoUrl = conteudo.arquivoUrl,
                        aulaId = aula.id.value.toString()
                    )
                }
            } else null
            
            AulaCompletaResponse(
                id = aula.id.value.toString(),
                titulo = aula.titulo,
                descricao = aula.descricao,
                ordem = aula.ordem,
                tipoConteudo = aula.tipoConteudo,
                planoId = aula.plano.id.value.toString(),
                moduloId = aula.modulo.id.value.toString(),
                conteudo = conteudo,
                temAcesso = temAcesso
            )
        }
    }
    
    fun getProdutosDisponiveis(alunoId: String, professorId: String): List<ProdutoResponse> {
        return transaction {
            // Buscar planos ativos do aluno para este professor
            val planosAtivos = InscricaoEntity.find {
                (Inscricoes.alunoId eq UUID.fromString(alunoId)) and
                (Inscricoes.status eq StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true)
            }.map { it.plano.id.value }
            
            if (planosAtivos.isEmpty()) {
                return@transaction emptyList()
            }
            
            // Buscar produtos associados aos planos ativos
            val produtoIds = ProdutoPlanoEntity.find {
                (ProdutoPlanos.planoId inList planosAtivos) and (ProdutoPlanos.isActive eq true)
            }.map { it.produto.id.value }.distinct()
            
            ProdutoEntity.find {
                (Produtos.id inList produtoIds) and 
                (Produtos.professorId eq UUID.fromString(professorId)) and 
                (Produtos.isActive eq true)
            }.map { produto ->
                ProdutoResponse(
                    id = produto.id.value.toString(),
                    titulo = produto.titulo,
                    descricao = produto.descricao,
                    tipo = produto.tipo,
                    capaUrl = produto.capaUrl,
                    videoIntroUrl = produto.videoIntroUrl,
                    professorId = produto.professor.id.value.toString(),
                    professorNome = produto.professor.nome
                )
            }
        }
    }
    
    // ========== PERFIL ==========
    
    fun getMeuPerfil(alunoId: String): AlunoResponse {
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            AlunoResponse(
                id = aluno.id.value.toString(),
                nome = aluno.nome,
                email = aluno.email,
                fotoPerfilUrl = aluno.fotoPerfilUrl
            )
        }
    }
    
    fun updateMeuPerfil(alunoId: String, request: AlunoUpdateRequest): AlunoResponse {
        return transaction {
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            request.nome?.let { aluno.nome = it }
            request.fotoPerfilUrl?.let { aluno.fotoPerfilUrl = it }
            
            AlunoResponse(
                id = aluno.id.value.toString(),
                nome = aluno.nome,
                email = aluno.email,
                fotoPerfilUrl = aluno.fotoPerfilUrl
            )
        }
    }
    
    // ========== UTILITÁRIOS ==========
    
    private fun hasAccessToPlano(alunoId: String, planoId: String): Boolean {
        return transaction {
            InscricaoEntity.find {
                (Inscricoes.alunoId eq UUID.fromString(alunoId)) and
                (Inscricoes.planoId eq UUID.fromString(planoId)) and
                (Inscricoes.status eq StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true)
            }.firstOrNull() != null
        }
    }
}

/**
 * Response para aula completa com verificação de acesso
 */
@kotlinx.serialization.Serializable
data class AulaCompletaResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: com.example.treine_me.enums.TipoConteudo,
    val planoId: String,
    val moduloId: String,
    val conteudo: ConteudoResponse? = null, // Só presente se o aluno tiver acesso
    val temAcesso: Boolean // Indica se o aluno tem acesso ao conteúdo
)
