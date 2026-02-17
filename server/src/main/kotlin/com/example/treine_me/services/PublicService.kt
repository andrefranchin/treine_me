package com.example.treine_me.services

import com.example.treine_me.exceptions.NotFoundException
import com.example.treine_me.models.*
import com.example.treine_me.auth.JwtConfig
import com.example.treine_me.enums.StatusInscricao
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Serviço público para funcionalidades que não requerem autenticação.
 * Permite que usuários não logados naveguem pelo conteúdo de um professor específico,
 * mas sem acesso ao conteúdo das aulas.
 * 
 * IMPORTANTE: Cada professor tem seu próprio app, então todos os métodos
 * são sempre filtrados por professorId.
 */
class PublicService {
    
    // ========== PROFESSOR ==========
    
    fun getProfessor(professorId: String): ProfessorResponse {
        return transaction {
            val professor = ProfessorEntity.find { 
                (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
            
            ProfessorResponse(
                id = professor.id.value.toString(),
                nome = professor.nome,
                email = professor.email,
                bio = professor.bio,
                fotoPerfilUrl = professor.fotoPerfilUrl
            )
        }
    }
    
    // ========== PLANOS ==========
    
    fun listPlanos(professorId: String, page: Int = 1, size: Int = 20): PaginatedResponse<PlanoResponse> {
        return transaction {
            // Validar se o professor existe
            validateProfessorExists(professorId)
            
            val offset = (page - 1) * size
            
            val planos = PlanoEntity.find {
                (Planos.professorId eq UUID.fromString(professorId)) and (Planos.isActive eq true)
            }.drop(offset)
                .take(size)
                .map { plano ->
                PlanoResponse(
                    id = plano.id.value.toString(),
                    nome = plano.nome,
                    descricao = plano.descricao,
                    valor = plano.valor.toString(),
                    recorrencia = plano.recorrencia,
                    professorId = plano.professor.id.value.toString(),
                    professorNome = plano.professor.nome
                )
            }
            
            val total = PlanoEntity.find { 
                (Planos.professorId eq UUID.fromString(professorId)) and (Planos.isActive eq true) 
            }.count()
            
            PaginatedResponse(
                data = planos,
                page = page,
                size = size,
                total = total,
                totalPages = (total + size - 1) / size
            )
        }
    }
    
    fun getPlano(planoId: String, professorId: String): PlanoResponse {
        return transaction {
            val plano = PlanoEntity.find { 
                (Planos.id eq UUID.fromString(planoId)) and 
                (Planos.professorId eq UUID.fromString(professorId)) and 
                (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            
            PlanoResponse(
                id = plano.id.value.toString(),
                nome = plano.nome,
                descricao = plano.descricao,
                valor = plano.valor.toString(),
                recorrencia = plano.recorrencia,
                professorId = plano.professor.id.value.toString(),
                professorNome = plano.professor.nome
            )
        }
    }
    
    // ========== PRODUTOS ==========
    
    fun listProdutos(professorId: String, page: Int = 1, size: Int = 20): PaginatedResponse<ProdutoResponse> {
        return transaction {
            // Validar se o professor existe
            validateProfessorExists(professorId)
            
            val offset = (page - 1) * size
            
            val produtos = ProdutoEntity.find {
                (Produtos.professorId eq UUID.fromString(professorId)) and (Produtos.isActive eq true)
            }.drop(offset)
                .take(size)
                .map { produto ->
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
            
            val total = ProdutoEntity.find { 
                (Produtos.professorId eq UUID.fromString(professorId)) and (Produtos.isActive eq true) 
            }.count()
            
            PaginatedResponse(
                data = produtos,
                page = page,
                size = size,
                total = total,
                totalPages = (total + size - 1) / size
            )
        }
    }
    
    fun getProduto(produtoId: String, professorId: String): ProdutoResponse {
        return transaction {
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and 
                (Produtos.professorId eq UUID.fromString(professorId)) and 
                (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            
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
    
    fun listProdutosByPlano(planoId: String, professorId: String): List<ProdutoResponse> {
        return transaction {
            PlanoEntity.find { 
                (Planos.id eq UUID.fromString(planoId)) and 
                (Planos.professorId eq UUID.fromString(professorId)) and 
                (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            
            ProdutoPlanoEntity.find { 
                (ProdutoPlanos.planoId eq UUID.fromString(planoId)) and (ProdutoPlanos.isActive eq true) 
            }.map { association ->
                val produto = association.produto
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
    
    // ========== MÓDULOS ==========
    
    fun listModulosByProduto(produtoId: String, professorId: String, token: String? = null): List<PublicModuloResponse> {
        return transaction {
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and 
                (Produtos.professorId eq UUID.fromString(professorId)) and 
                (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            
            // Verificar se há um aluno logado
            val alunoId = getAlunoIdFromToken(token)
            
            ModuloEntity.find { 
                (Modulos.produtoId eq produto.id) and (Modulos.isActive eq true) 
            }.sortedBy { it.ordem }.map { modulo ->
                val moduloId = modulo.id.value.toString()
                
                // Calcular progresso do módulo se o aluno estiver logado
                val progressoModulo = if (alunoId != null) {
                    calcularProgressoModulo(alunoId, moduloId)
                } else null
                
                PublicModuloResponse(
                    id = moduloId,
                    titulo = modulo.titulo,
                    descricao = modulo.descricao,
                    ordem = modulo.ordem,
                    capaUrl = modulo.capaUrl,
                    videoIntroUrl = modulo.videoIntroUrl,
                    produtoId = produto.id.value.toString(),
                    aulas = emptyList(), // Aulas serão carregadas separadamente
                    progressoModulo = progressoModulo
                )
            }
        }
    }
    
    fun getModulo(moduloId: String, professorId: String, token: String? = null): PublicModuloResponse {
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            
            // Verificar se o módulo pertence ao professor
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw NotFoundException("Módulo não encontrado")
            }
            
            // Verificar se há um aluno logado
            val alunoId = getAlunoIdFromToken(token)
            
            // Calcular progresso do módulo se o aluno estiver logado
            val progressoModulo = if (alunoId != null) {
                calcularProgressoModulo(alunoId, moduloId)
            } else null
            
            PublicModuloResponse(
                id = modulo.id.value.toString(),
                titulo = modulo.titulo,
                descricao = modulo.descricao,
                ordem = modulo.ordem,
                capaUrl = modulo.capaUrl,
                videoIntroUrl = modulo.videoIntroUrl,
                produtoId = modulo.produto.id.value.toString(),
                aulas = emptyList(), // Aulas serão carregadas separadamente
                progressoModulo = progressoModulo
            )
        }
    }
    
    // ========== AULAS ==========
    
    fun listAulasByModulo(moduloId: String, professorId: String, token: String? = null): List<PublicAulaResponse> {
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            
            // Verificar se o módulo pertence ao professor
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw NotFoundException("Módulo não encontrado")
            }
            
            // Verificar se há um aluno logado
            val alunoId = getAlunoIdFromToken(token)
            
            AulaEntity.find { (Aulas.moduloId eq modulo.id) and (Aulas.isActive eq true) }
                .sortedBy { it.ordem }
                .map { aula ->
                    val aulaId = aula.id.value.toString()
                    val planoId = aula.plano.id.value.toString()
                    
                    // Verificar se o aluno tem acesso a esta aula
                    val temAcesso = alunoId?.let { 
                        alunoTemAcessoAoPlano(it, planoId) 
                    } ?: false
                    
                    // Obter progresso do aluno se estiver logado e tiver acesso
                    val progresso = if (alunoId != null && temAcesso) {
                        getProgressoAula(alunoId, aulaId)
                    } else null
                    
                    PublicAulaResponse(
                        id = aulaId,
                        titulo = aula.titulo,
                        descricao = aula.descricao,
                        ordem = aula.ordem,
                        tipoConteudo = aula.tipoConteudo,
                        planoId = planoId,
                        moduloId = modulo.id.value.toString(),
                        // Não incluímos o conteúdo nas listagens públicas
                        temConteudo = hasConteudo(aula.id.value),
                        
                        // Metadados do vídeo
                        videoDuracaoSegundos = aula.videoDuracaoSegundos,
                        videoResolucao = aula.videoResolucao,
                        videoTamanhoBytes = aula.videoTamanhoBytes,
                        videoCodec = aula.videoCodec,
                        videoFps = aula.videoFps,
                        videoAspectRatio = aula.videoAspectRatio,
                        
                        // Configurações do treino
                        caloriasPerdidas = aula.caloriasPerdidas,
                        dificuldade = aula.dificuldade,
                        tipoTreino = aula.tipoTreino,
                        equipamentosNecessarios = aula.equipamentosNecessarios,
                        duracaoTreinoMinutos = aula.duracaoTreinoMinutos,
                        intensidade = aula.intensidade,
                        observacoesTreino = aula.observacoesTreino,
                        
                        // Campos para alunos logados
                        temAcesso = temAcesso,
                        progresso = progresso
                    )
                }
        }
    }
    
    fun getAula(aulaId: String, professorId: String, token: String? = null): PublicAulaResponse {
        return transaction {
            val aula = AulaEntity.find { (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) }
                .firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            
            // Verificar se a aula pertence ao professor
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw NotFoundException("Aula não encontrada")
            }
            
            val planoId = aula.plano.id.value.toString()
            
            // Verificar se há um aluno logado
            val alunoId = getAlunoIdFromToken(token)
            
            // Verificar se o aluno tem acesso a esta aula
            val temAcesso = alunoId?.let { 
                alunoTemAcessoAoPlano(it, planoId) 
            } ?: false
            
            // Obter progresso do aluno se estiver logado e tiver acesso
            val progresso = if (alunoId != null && temAcesso) {
                getProgressoAula(alunoId, aulaId)
            } else null
            
            PublicAulaResponse(
                id = aula.id.value.toString(),
                titulo = aula.titulo,
                descricao = aula.descricao,
                ordem = aula.ordem,
                tipoConteudo = aula.tipoConteudo,
                planoId = planoId,
                moduloId = aula.modulo.id.value.toString(),
                temConteudo = hasConteudo(aula.id.value),
                
                // Metadados do vídeo
                videoDuracaoSegundos = aula.videoDuracaoSegundos,
                videoResolucao = aula.videoResolucao,
                videoTamanhoBytes = aula.videoTamanhoBytes,
                videoCodec = aula.videoCodec,
                videoFps = aula.videoFps,
                videoAspectRatio = aula.videoAspectRatio,
                
                // Configurações do treino
                caloriasPerdidas = aula.caloriasPerdidas,
                dificuldade = aula.dificuldade,
                tipoTreino = aula.tipoTreino,
                equipamentosNecessarios = aula.equipamentosNecessarios,
                duracaoTreinoMinutos = aula.duracaoTreinoMinutos,
                intensidade = aula.intensidade,
                observacoesTreino = aula.observacoesTreino,
                
                // Campos para alunos logados
                temAcesso = temAcesso,
                progresso = progresso
            )
        }
    }
    
    // ========== UTILITÁRIOS ==========
    
    private fun validateProfessorExists(professorId: String) {
        ProfessorEntity.find { 
            (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
        }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
    }
    
    private fun hasConteudo(aulaId: UUID): Boolean {
        return transaction {
            ConteudoEntity.find { 
                (Conteudos.aulaId eq aulaId) and (Conteudos.isActive eq true) 
            }.firstOrNull() != null
        }
    }
    
    /**
     * Extrai o ID do aluno de um token JWT, se válido
     */
    private fun getAlunoIdFromToken(token: String?): String? {
        if (token.isNullOrBlank()) return null
        
        return try {
            val cleanToken = if (token.startsWith("Bearer ")) {
                token.substring(7)
            } else {
                token
            }
            
            val verifier = com.auth0.jwt.JWT
                .require(com.auth0.jwt.algorithms.Algorithm.HMAC256(JwtConfig.getSecret()))
                .withIssuer(JwtConfig.getIssuer())
                .withAudience(JwtConfig.getAudience())
                .build()
            
            val decodedJWT = verifier.verify(cleanToken)
            val role = decodedJWT.getClaim("role")?.asString()
            
            if (role == "ALUNO") {
                decodedJWT.getClaim("userId")?.asString()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Verifica se um aluno tem acesso a um plano específico
     */
    private fun alunoTemAcessoAoPlano(alunoId: String, planoId: String): Boolean {
        return transaction {
            InscricaoEntity.find {
                (Inscricoes.alunoId eq UUID.fromString(alunoId)) and
                (Inscricoes.planoId eq UUID.fromString(planoId)) and
                (Inscricoes.status eq StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true)
            }.firstOrNull() != null
        }
    }
    
    /**
     * Obtém o progresso de um aluno em uma aula específica
     */
    private fun getProgressoAula(alunoId: String, aulaId: String): ProgressoAulaInfo? {
        return transaction {
            ProgressoAulaEntity.find {
                (ProgressosAula.alunoId eq UUID.fromString(alunoId)) and
                (ProgressosAula.aulaId eq UUID.fromString(aulaId)) and
                (ProgressosAula.isActive eq true)
            }.firstOrNull()?.let { progresso ->
                ProgressoAulaInfo(
                    minutosTotaisAssistidos = progresso.minutosTotaisAssistidos,
                    ultimoMinutoAssistido = progresso.ultimoMinutoAssistido,
                    percentualConcluido = progresso.percentualConcluido,
                    concluida = progresso.concluida,
                    dataUltimaVisualizacao = progresso.dataUltimaVisualizacao
                )
            }
        }
    }
    
    /**
     * Calcula o progresso de um aluno em um módulo específico
     */
    private fun calcularProgressoModulo(alunoId: String, moduloId: String): ProgressoModuloInfo? {
        return transaction {
            // Buscar todas as aulas do módulo
            val aulas = AulaEntity.find { 
                (Aulas.moduloId eq UUID.fromString(moduloId)) and (Aulas.isActive eq true) 
            }.toList()
            
            if (aulas.isEmpty()) return@transaction null
            
            // Buscar progressos do aluno para essas aulas
            val progressos = ProgressoAulaEntity.find {
                (ProgressosAula.alunoId eq UUID.fromString(alunoId)) and
                (ProgressosAula.isActive eq true)
            }.filter { progresso ->
                aulas.any { aula -> aula.id == progresso.aula.id }
            }
            
            val totalAulas = aulas.size
            val aulasAssistidas = progressos.count { it.minutosTotaisAssistidos > 0 }
            val aulasConcluidas = progressos.count { it.concluida }
            val percentualConcluido = if (totalAulas > 0) {
                (aulasConcluidas * 100) / totalAulas
            } else 0
            
            ProgressoModuloInfo(
                totalAulas = totalAulas,
                aulasAssistidas = aulasAssistidas,
                aulasConcluidas = aulasConcluidas,
                percentualConcluido = percentualConcluido
            )
        }
    }
}

/**
 * Response para aulas em contexto público.
 * Não inclui o conteúdo real, apenas metadados.
 * Inclui informações de acesso e progresso quando o aluno está logado.
 */
@kotlinx.serialization.Serializable
data class PublicAulaResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: com.example.treine_me.enums.TipoConteudo,
    val planoId: String,
    val moduloId: String,
    val temConteudo: Boolean, // Indica se a aula tem conteúdo disponível
    
    // Metadados do vídeo
    val videoDuracaoSegundos: Int? = null,
    val videoResolucao: String? = null,
    val videoTamanhoBytes: Long? = null,
    val videoCodec: String? = null,
    val videoFps: Int? = null,
    val videoAspectRatio: String? = null,
    
    // Configurações do treino
    val caloriasPerdidas: Int? = null,
    val dificuldade: com.example.treine_me.enums.DificuldadeTreino? = null,
    val tipoTreino: com.example.treine_me.enums.TipoTreino? = null,
    val equipamentosNecessarios: String? = null,
    val duracaoTreinoMinutos: Int? = null,
    val intensidade: Int? = null,
    val observacoesTreino: String? = null,
    
    // Campos para alunos logados
    val temAcesso: Boolean = false, // Se o aluno tem acesso a esta aula
    val progresso: ProgressoAulaInfo? = null // Progresso do aluno nesta aula
)

/**
 * Response para módulos em contexto público.
 * Inclui informações de progresso quando o aluno está logado.
 */
@kotlinx.serialization.Serializable
data class PublicModuloResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val capaUrl: String? = null,
    val videoIntroUrl: String? = null,
    val produtoId: String,
    val aulas: List<PublicAulaResponse> = emptyList(),
    
    // Campos para alunos logados
    val progressoModulo: ProgressoModuloInfo? = null // Progresso do aluno neste módulo
)

/**
 * Informações de progresso do aluno em uma aula
 */
@kotlinx.serialization.Serializable
data class ProgressoAulaInfo(
    val minutosTotaisAssistidos: Int = 0,
    val ultimoMinutoAssistido: Int = 0,
    val percentualConcluido: Int = 0,
    val concluida: Boolean = false,
    val dataUltimaVisualizacao: kotlinx.datetime.Instant? = null
)

/**
 * Informações de progresso do aluno em um módulo
 */
@kotlinx.serialization.Serializable
data class ProgressoModuloInfo(
    val totalAulas: Int = 0,
    val aulasAssistidas: Int = 0,
    val aulasConcluidas: Int = 0,
    val percentualConcluido: Int = 0
)
