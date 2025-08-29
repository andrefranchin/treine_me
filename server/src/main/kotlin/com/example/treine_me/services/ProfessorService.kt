package com.example.treine_me.services

import com.example.treine_me.exceptions.ConflictException
import com.example.treine_me.exceptions.ForbiddenException
import com.example.treine_me.exceptions.NotFoundException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.util.*

class ProfessorService {
    
    // ========== PLANOS ==========
    
    fun createPlano(request: PlanoCreateRequest, professorId: String): PlanoResponse {
        validatePlanoRequest(request)
        
        return transaction {
            val professor = getProfessorEntity(professorId)
            
            val now = Clock.System.now()
            val plano = PlanoEntity.new {
                nome = request.nome
                descricao = request.descricao
                valor = BigDecimal(request.valor)
                recorrencia = request.recorrencia
                this.professor = professor
                dtIns = now
                dtUpd = now
                idUserIns = UUID.fromString(professorId)
                idUserUpd = UUID.fromString(professorId)
                isActive = true
            }
            
            PlanoResponse(
                id = plano.id.value.toString(),
                nome = plano.nome,
                descricao = plano.descricao,
                valor = plano.valor.toString(),
                recorrencia = plano.recorrencia,
                professorId = professor.id.value.toString(),
                professorNome = professor.nome
            )
        }
    }
    
    fun listPlanos(professorId: String, page: Int = 1, size: Int = 20): PaginatedResponse<PlanoResponse> {
        return transaction {
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
                (Planos.id eq UUID.fromString(planoId)) and (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            
            if (plano.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para acessar este plano")
            }
            
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
    
    fun updatePlano(planoId: String, request: PlanoUpdateRequest, professorId: String): PlanoResponse {
        return transaction {
            val plano = PlanoEntity.find { 
                (Planos.id eq UUID.fromString(planoId)) and (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            
            if (plano.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para editar este plano")
            }
            
            val now = Clock.System.now()
            
            request.nome?.let { plano.nome = it }
            request.descricao?.let { plano.descricao = it }
            request.valor?.let { plano.valor = BigDecimal(it) }
            request.recorrencia?.let { plano.recorrencia = it }
            
            plano.dtUpd = now
            plano.idUserUpd = UUID.fromString(professorId)
            
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
    
    fun deactivatePlano(planoId: String, professorId: String): Boolean {
        return transaction {
            val plano = PlanoEntity.find { 
                (Planos.id eq UUID.fromString(planoId)) and (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            
            if (plano.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para excluir este plano")
            }
            
            val now = Clock.System.now()
            plano.isActive = false
            plano.dtUpd = now
            plano.idUserUpd = UUID.fromString(professorId)
            
            true
        }
    }
    
    // ========== PRODUTOS ==========
    
    fun createProduto(request: ProdutoCreateRequest, professorId: String): ProdutoResponse {
        validateProdutoRequest(request)
        
        return transaction {
            val professor = getProfessorEntity(professorId)
            
            val now = Clock.System.now()
            val produto = ProdutoEntity.new {
                titulo = request.titulo
                descricao = request.descricao
                tipo = request.tipo
                capaUrl = request.capaUrl
                videoIntroUrl = request.videoIntroUrl
                this.professor = professor
                dtIns = now
                dtUpd = now
                idUserIns = UUID.fromString(professorId)
                idUserUpd = UUID.fromString(professorId)
                isActive = true
            }
            
            ProdutoResponse(
                id = produto.id.value.toString(),
                titulo = produto.titulo,
                descricao = produto.descricao,
                tipo = produto.tipo,
                capaUrl = produto.capaUrl,
                videoIntroUrl = produto.videoIntroUrl,
                professorId = professor.id.value.toString(),
                professorNome = professor.nome
            )
        }
    }
    
    fun listProdutos(professorId: String, page: Int = 1, size: Int = 20): PaginatedResponse<ProdutoResponse> {
        return transaction {
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
                (Produtos.id eq UUID.fromString(produtoId)) and (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            
            if (produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para acessar este produto")
            }
            
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
    
    fun updateProduto(produtoId: String, request: ProdutoUpdateRequest, professorId: String): ProdutoResponse {
        return transaction {
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            
            if (produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para editar este produto")
            }
            
            val now = Clock.System.now()
            
            request.titulo?.let { produto.titulo = it }
            request.descricao?.let { produto.descricao = it }
            request.tipo?.let { produto.tipo = it }
            request.capaUrl?.let { produto.capaUrl = it }
            request.videoIntroUrl?.let { produto.videoIntroUrl = it }
            
            produto.dtUpd = now
            produto.idUserUpd = UUID.fromString(professorId)
            
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
    
    fun deactivateProduto(produtoId: String, professorId: String): Boolean {
        return transaction {
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            
            if (produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para excluir este produto")
            }
            
            val now = Clock.System.now()
            produto.isActive = false
            produto.dtUpd = now
            produto.idUserUpd = UUID.fromString(professorId)
            
            true
        }
    }
    
    // ========== ASSOCIAÇÃO PRODUTO-PLANO ==========
    
    fun addProdutoToPlano(planoId: String, produtoId: String, professorId: String): Boolean {
        return transaction {
            val plano = PlanoEntity.find { 
                (Planos.id eq UUID.fromString(planoId)) and (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            
            if (plano.professor.id.value.toString() != professorId || produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para fazer esta associação")
            }
            
            // Verificar se associação já existe
            val existingAssociation = ProdutoPlanoEntity.find { 
                (ProdutoPlanos.planoId eq UUID.fromString(planoId)) and 
                (ProdutoPlanos.produtoId eq UUID.fromString(produtoId)) and 
                (ProdutoPlanos.isActive eq true) 
            }.firstOrNull()
            
            if (existingAssociation != null) {
                throw ConflictException("Produto já está associado a este plano")
            }
            
            val now = Clock.System.now()
            ProdutoPlanoEntity.new {
                this.plano = plano
                this.produto = produto
                dtIns = now
                dtUpd = now
                idUserIns = UUID.fromString(professorId)
                idUserUpd = UUID.fromString(professorId)
                isActive = true
            }
            
            true
        }
    }
    
    fun removeProdutoFromPlano(planoId: String, produtoId: String, professorId: String): Boolean {
        return transaction {
            val association = ProdutoPlanoEntity.find { 
                (ProdutoPlanos.planoId eq UUID.fromString(planoId)) and 
                (ProdutoPlanos.produtoId eq UUID.fromString(produtoId)) and 
                (ProdutoPlanos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Associação não encontrada")
            
            if (association.plano.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para remover esta associação")
            }
            
            val now = Clock.System.now()
            association.isActive = false
            association.dtUpd = now
            association.idUserUpd = UUID.fromString(professorId)
            
            true
        }
    }
    
    fun listProdutosByPlano(planoId: String, professorId: String): List<ProdutoResponse> {
        return transaction {
            val plano = PlanoEntity.find { 
                (Planos.id eq UUID.fromString(planoId)) and (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            
            if (plano.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para acessar este plano")
            }
            
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
    
    // ========== ATRIBUIR PLANOS A ALUNOS ==========
    
    fun assignPlanoToAluno(planoId: String, alunoId: String, professorId: String): InscricaoResponse {
        return transaction {
            val plano = PlanoEntity.find { 
                (Planos.id eq UUID.fromString(planoId)) and (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            
            val aluno = AlunoEntity.find { 
                (Alunos.id eq UUID.fromString(alunoId)) and (Alunos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Aluno não encontrado")
            
            if (plano.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para atribuir este plano")
            }
            
            // Verificar se já existe inscrição ativa
            val existingInscricao = InscricaoEntity.find { 
                (Inscricoes.alunoId eq UUID.fromString(alunoId)) and 
                (Inscricoes.planoId eq UUID.fromString(planoId)) and 
                (Inscricoes.status eq com.example.treine_me.enums.StatusInscricao.ATIVA) and
                (Inscricoes.isActive eq true) 
            }.firstOrNull()
            
            if (existingInscricao != null) {
                throw ConflictException("Aluno já possui inscrição ativa neste plano")
            }
            
            val now = Clock.System.now()
            val inscricao = InscricaoEntity.new {
                dtInicio = now
                dtFim = null // Plano indefinido por enquanto
                status = com.example.treine_me.enums.StatusInscricao.ATIVA
                this.aluno = aluno
                this.plano = plano
                dtIns = now
                dtUpd = now
                idUserIns = UUID.fromString(professorId)
                idUserUpd = UUID.fromString(professorId)
                isActive = true
            }
            
            InscricaoResponse(
                id = inscricao.id.value.toString(),
                dtInicio = inscricao.dtInicio,
                dtFim = inscricao.dtFim,
                status = inscricao.status,
                aluno = AlunoResponse(
                    id = aluno.id.value.toString(),
                    nome = aluno.nome,
                    email = aluno.email,
                    fotoPerfilUrl = aluno.fotoPerfilUrl
                ),
                plano = PlanoResponse(
                    id = plano.id.value.toString(),
                    nome = plano.nome,
                    descricao = plano.descricao,
                    valor = plano.valor.toString(),
                    recorrencia = plano.recorrencia,
                    professorId = plano.professor.id.value.toString(),
                    professorNome = plano.professor.nome
                )
            )
        }
    }
    
    // ========== UTILITÁRIOS ==========
    
    private fun getProfessorEntity(professorId: String): ProfessorEntity {
        return ProfessorEntity.find { 
            (Professores.id eq UUID.fromString(professorId)) and (Professores.isActive eq true) 
        }.firstOrNull() ?: throw NotFoundException("Professor não encontrado")
    }
    
    private fun validatePlanoRequest(request: PlanoCreateRequest) {
        if (request.nome.isBlank()) {
            throw ValidationException("Nome é obrigatório", "nome")
        }
        if (request.descricao.isBlank()) {
            throw ValidationException("Descrição é obrigatória", "descricao")
        }
        try {
            val valor = BigDecimal(request.valor)
            if (valor <= BigDecimal.ZERO) {
                throw ValidationException("Valor deve ser maior que zero", "valor")
            }
        } catch (e: NumberFormatException) {
            throw ValidationException("Valor deve ser um número válido", "valor")
        }
    }
    
    private fun validateProdutoRequest(request: ProdutoCreateRequest) {
        if (request.titulo.isBlank()) {
            throw ValidationException("Título é obrigatório", "titulo")
        }
        if (request.descricao.isBlank()) {
            throw ValidationException("Descrição é obrigatória", "descricao")
        }
    }
}
