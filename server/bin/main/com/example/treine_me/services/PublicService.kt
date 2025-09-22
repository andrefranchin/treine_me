package com.example.treine_me.services

import com.example.treine_me.exceptions.NotFoundException
import com.example.treine_me.models.*
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
            val plano = PlanoEntity.find { 
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
    
    fun listModulosByProduto(produtoId: String, professorId: String): List<ModuloResponse> {
        return transaction {
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and 
                (Produtos.professorId eq UUID.fromString(professorId)) and 
                (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            
            ModuloEntity.find { 
                (Modulos.produtoId eq produto.id) and (Modulos.isActive eq true) 
            }.sortedBy { it.ordem }.map { modulo ->
                ModuloResponse(
                    id = modulo.id.value.toString(),
                    titulo = modulo.titulo,
                    descricao = modulo.descricao,
                    ordem = modulo.ordem,
                    capaUrl = modulo.capaUrl,
                    videoIntroUrl = modulo.videoIntroUrl,
                    produtoId = produto.id.value.toString(),
                    aulas = emptyList() // Aulas serão carregadas separadamente
                )
            }
        }
    }
    
    fun getModulo(moduloId: String, professorId: String): ModuloResponse {
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            
            // Verificar se o módulo pertence ao professor
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw NotFoundException("Módulo não encontrado")
            }
            
            ModuloResponse(
                id = modulo.id.value.toString(),
                titulo = modulo.titulo,
                descricao = modulo.descricao,
                ordem = modulo.ordem,
                capaUrl = modulo.capaUrl,
                videoIntroUrl = modulo.videoIntroUrl,
                produtoId = modulo.produto.id.value.toString(),
                aulas = emptyList() // Aulas serão carregadas separadamente
            )
        }
    }
    
    // ========== AULAS ==========
    
    fun listAulasByModulo(moduloId: String, professorId: String): List<PublicAulaResponse> {
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            
            // Verificar se o módulo pertence ao professor
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw NotFoundException("Módulo não encontrado")
            }
            
            AulaEntity.find { (Aulas.moduloId eq modulo.id) and (Aulas.isActive eq true) }
                .sortedBy { it.ordem }
                .map { aula ->
                    PublicAulaResponse(
                        id = aula.id.value.toString(),
                        titulo = aula.titulo,
                        descricao = aula.descricao,
                        ordem = aula.ordem,
                        tipoConteudo = aula.tipoConteudo,
                        planoId = aula.plano.id.value.toString(),
                        moduloId = modulo.id.value.toString(),
                        // Não incluímos o conteúdo nas listagens públicas
                        temConteudo = hasConteudo(aula.id.value)
                    )
                }
        }
    }
    
    fun getAula(aulaId: String, professorId: String): PublicAulaResponse {
        return transaction {
            val aula = AulaEntity.find { (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) }
                .firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            
            // Verificar se a aula pertence ao professor
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw NotFoundException("Aula não encontrada")
            }
            
            PublicAulaResponse(
                id = aula.id.value.toString(),
                titulo = aula.titulo,
                descricao = aula.descricao,
                ordem = aula.ordem,
                tipoConteudo = aula.tipoConteudo,
                planoId = aula.plano.id.value.toString(),
                moduloId = aula.modulo.id.value.toString(),
                temConteudo = hasConteudo(aula.id.value)
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
}

/**
 * Response para aulas em contexto público.
 * Não inclui o conteúdo real, apenas metadados.
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
    val temConteudo: Boolean // Indica se a aula tem conteúdo disponível
)
