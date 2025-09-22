package com.example.treine_me.services

import com.example.treine_me.exceptions.ConflictException
import com.example.treine_me.exceptions.ForbiddenException
import com.example.treine_me.exceptions.NotFoundException
import com.example.treine_me.exceptions.ValidationException
import com.example.treine_me.models.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.coroutines.runBlocking
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
    
    // ========== MÓDULOS ==========
    
    fun createModulo(produtoId: String, request: ModuloCreateRequest, professorId: String): ModuloResponse {
        return transaction {
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            
            if (produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para adicionar módulo neste produto")
            }
            
            val now = Clock.System.now()
            val ordem = request.ordem ?: run {
                val lastOrder = ModuloEntity.find { 
                    (Modulos.produtoId eq produto.id) and (Modulos.isActive eq true) 
                }.maxOfOrNull { it.ordem } ?: 0
                lastOrder + 1
            }
            
            val modulo = ModuloEntity.new {
                titulo = request.titulo
                descricao = request.descricao
                this.ordem = ordem
                capaUrl = request.capaUrl
                videoIntroUrl = request.videoIntroUrl
                this.produto = produto
                dtIns = now
                dtUpd = now
                idUserIns = UUID.fromString(professorId)
                idUserUpd = UUID.fromString(professorId)
                isActive = true
            }
            
            ModuloResponse(
                id = modulo.id.value.toString(),
                titulo = modulo.titulo,
                descricao = modulo.descricao,
                ordem = modulo.ordem,
                capaUrl = modulo.capaUrl,
                videoIntroUrl = modulo.videoIntroUrl,
                produtoId = produto.id.value.toString(),
                aulas = emptyList()
            )
        }
    }
    
    fun listModulosByProduto(produtoId: String, professorId: String): List<ModuloResponse> {
        return transaction {
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            if (produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para acessar este produto")
            }
            
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
                    aulas = emptyList()
                )
            }
        }
    }
    
    fun updateModulo(moduloId: String, request: ModuloUpdateRequest, professorId: String): ModuloResponse {
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para editar este módulo")
            }
            
            val now = Clock.System.now()
            request.titulo?.let { modulo.titulo = it }
            request.descricao?.let { modulo.descricao = it }
            request.ordem?.let { modulo.ordem = it }
            request.capaUrl?.let { modulo.capaUrl = it }
            request.videoIntroUrl?.let { modulo.videoIntroUrl = it }
            modulo.dtUpd = now
            modulo.idUserUpd = UUID.fromString(professorId)
            
            ModuloResponse(
                id = modulo.id.value.toString(),
                titulo = modulo.titulo,
                descricao = modulo.descricao,
                ordem = modulo.ordem,
                capaUrl = modulo.capaUrl,
                videoIntroUrl = modulo.videoIntroUrl,
                produtoId = modulo.produto.id.value.toString(),
                aulas = emptyList()
            )
        }
    }
    
    fun deactivateModulo(moduloId: String, professorId: String): Boolean {
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para excluir este módulo")
            }
            val now = Clock.System.now()
            modulo.isActive = false
            modulo.dtUpd = now
            modulo.idUserUpd = UUID.fromString(professorId)
            true
        }
    }
    
    fun reorderModulos(produtoId: String, moduloIds: List<String>, professorId: String): Boolean {
        return transaction {
            val produto = ProdutoEntity.find { 
                (Produtos.id eq UUID.fromString(produtoId)) and (Produtos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Produto não encontrado")
            if (produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para editar este produto")
            }
            val idSet = moduloIds.map { UUID.fromString(it) }.toSet()
            val modulos = ModuloEntity.find { (Modulos.produtoId eq produto.id) and (Modulos.isActive eq true) }
            if (modulos.count().toInt() != idSet.size) {
                // Permitir reordenar subconjunto? Por simplicidade, exigir lista completa
                throw ValidationException("A lista de módulos deve conter todos os módulos do produto", "moduloIds")
            }
            val now = Clock.System.now()
            moduloIds.forEachIndexed { index, idStr ->
                val uuid = UUID.fromString(idStr)
                val modulo = ModuloEntity.findById(uuid) ?: return@forEachIndexed
                if (modulo.produto.id != produto.id) {
                    throw ValidationException("Módulo não pertence ao produto", "moduloIds")
                }
                modulo.ordem = index + 1
                modulo.dtUpd = now
                modulo.idUserUpd = UUID.fromString(professorId)
            }
            true
        }
    }
    
    // ========== AULAS ==========
    
    fun createAula(moduloId: String, request: AulaCreateRequest, professorId: String): AulaResponse {
        if (request.planoId.isBlank()) {
            throw ValidationException("planoId é obrigatório", "planoId")
        }
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para adicionar aula neste módulo")
            }
            val plano = PlanoEntity.find { 
                (Planos.id eq UUID.fromString(request.planoId)) and (Planos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
            if (plano.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para usar este plano")
            }
            val now = Clock.System.now()
            val ordem = request.ordem ?: run {
                val lastOrder = AulaEntity.find { 
                    (Aulas.moduloId eq modulo.id) and (Aulas.isActive eq true) 
                }.maxOfOrNull { it.ordem } ?: 0
                lastOrder + 1
            }
            val aula = AulaEntity.new {
                titulo = request.titulo
                descricao = request.descricao
                this.ordem = ordem
                tipoConteudo = request.tipoConteudo
                this.plano = plano
                this.modulo = modulo
                
                // Configurações do treino
                caloriasPerdidas = request.caloriasPerdidas
                dificuldade = request.dificuldade
                tipoTreino = request.tipoTreino
                equipamentosNecessarios = request.equipamentosNecessarios
                duracaoTreinoMinutos = request.duracaoTreinoMinutos
                intensidade = request.intensidade
                observacoesTreino = request.observacoesTreino
                
                dtIns = now
                dtUpd = now
                idUserIns = UUID.fromString(professorId)
                idUserUpd = UUID.fromString(professorId)
                isActive = true
            }
            AulaResponse(
                id = aula.id.value.toString(),
                titulo = aula.titulo,
                descricao = aula.descricao,
                ordem = aula.ordem,
                tipoConteudo = aula.tipoConteudo,
                planoId = plano.id.value.toString(),
                moduloId = modulo.id.value.toString(),
                conteudo = null,
                
                // Metadados do vídeo (inicialmente nulos)
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
                observacoesTreino = aula.observacoesTreino
            )
        }
    }
    
    fun listAulasByModulo(moduloId: String, professorId: String): List<AulaResponse> {
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para acessar este módulo")
            }
            AulaEntity.find { (Aulas.moduloId eq modulo.id) and (Aulas.isActive eq true) }
                .sortedBy { it.ordem }
                .map { aula ->
                    // Buscar conteúdo da aula
                    val conteudo = ConteudoEntity.find { Conteudos.aulaId eq aula.id }.firstOrNull()
                    val conteudoResponse = conteudo?.let {
                        ConteudoResponse(
                            id = it.id.value.toString(),
                            urlVideo = it.urlVideo,
                            textoMarkdown = it.textoMarkdown,
                            arquivoUrl = it.arquivoUrl,
                            aulaId = aula.id.value.toString()
                        )
                    }
                    
                    AulaResponse(
                        id = aula.id.value.toString(),
                        titulo = aula.titulo,
                        descricao = aula.descricao,
                        ordem = aula.ordem,
                        tipoConteudo = aula.tipoConteudo,
                        planoId = aula.plano.id.value.toString(),
                        moduloId = modulo.id.value.toString(),
                        conteudo = conteudoResponse,
                        
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
                        observacoesTreino = aula.observacoesTreino
                    )
                }
        }
    }
    
    fun getAula(aulaId: String, professorId: String): AulaResponse {
        return transaction {
            val aula = AulaEntity.find { (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) }
                .firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para acessar esta aula")
            }
            
            // Buscar conteúdo da aula
            val conteudo = ConteudoEntity.find { Conteudos.aulaId eq aula.id }.firstOrNull()
            val conteudoResponse = conteudo?.let {
                ConteudoResponse(
                    id = it.id.value.toString(),
                    urlVideo = it.urlVideo,
                    textoMarkdown = it.textoMarkdown,
                    arquivoUrl = it.arquivoUrl,
                    aulaId = aula.id.value.toString()
                )
            }
            
            AulaResponse(
                id = aula.id.value.toString(),
                titulo = aula.titulo,
                descricao = aula.descricao,
                ordem = aula.ordem,
                tipoConteudo = aula.tipoConteudo,
                planoId = aula.plano.id.value.toString(),
                moduloId = aula.modulo.id.value.toString(),
                conteudo = conteudoResponse,
                
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
                observacoesTreino = aula.observacoesTreino
            )
        }
    }
    
    fun updateAula(aulaId: String, request: AulaUpdateRequest, professorId: String): AulaResponse {
        return transaction {
            val aula = AulaEntity.find { (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) }
                .firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para editar esta aula")
            }
            val now = Clock.System.now()
            request.titulo?.let { aula.titulo = it }
            request.descricao?.let { aula.descricao = it }
            request.ordem?.let { aula.ordem = it }
            request.tipoConteudo?.let { aula.tipoConteudo = it }
            request.planoId?.let { pid ->
                val plano = PlanoEntity.find { 
                    (Planos.id eq UUID.fromString(pid)) and (Planos.isActive eq true) 
                }.firstOrNull() ?: throw NotFoundException("Plano não encontrado")
                if (plano.professor.id.value.toString() != professorId) {
                    throw ForbiddenException("Você não tem permissão para usar este plano")
                }
                aula.plano = plano
            }
            
            // Atualizar configurações do treino
            request.caloriasPerdidas?.let { aula.caloriasPerdidas = it }
            request.dificuldade?.let { aula.dificuldade = it }
            request.tipoTreino?.let { aula.tipoTreino = it }
            request.equipamentosNecessarios?.let { aula.equipamentosNecessarios = it }
            request.duracaoTreinoMinutos?.let { aula.duracaoTreinoMinutos = it }
            request.intensidade?.let { aula.intensidade = it }
            request.observacoesTreino?.let { aula.observacoesTreino = it }
            
            aula.dtUpd = now
            aula.idUserUpd = UUID.fromString(professorId)
            AulaResponse(
                id = aula.id.value.toString(),
                titulo = aula.titulo,
                descricao = aula.descricao,
                ordem = aula.ordem,
                tipoConteudo = aula.tipoConteudo,
                planoId = aula.plano.id.value.toString(),
                moduloId = aula.modulo.id.value.toString(),
                conteudo = null,
                
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
                observacoesTreino = aula.observacoesTreino
            )
        }
    }
    
    fun deactivateAula(aulaId: String, professorId: String): Boolean {
        return transaction {
            val aula = AulaEntity.find { (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) }
                .firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para excluir esta aula")
            }
            val now = Clock.System.now()
            aula.isActive = false
            aula.dtUpd = now
            aula.idUserUpd = UUID.fromString(professorId)
            true
        }
    }
    
    fun reorderAulas(moduloId: String, aulaIds: List<String>, professorId: String): Boolean {
        return transaction {
            val modulo = ModuloEntity.find { 
                (Modulos.id eq UUID.fromString(moduloId)) and (Modulos.isActive eq true) 
            }.firstOrNull() ?: throw NotFoundException("Módulo não encontrado")
            if (modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para editar este módulo")
            }
            val idSet = aulaIds.map { UUID.fromString(it) }.toSet()
            val aulas = AulaEntity.find { (Aulas.moduloId eq modulo.id) and (Aulas.isActive eq true) }
            if (aulas.count().toInt() != idSet.size) {
                throw ValidationException("A lista de aulas deve conter todas as aulas do módulo", "aulaIds")
            }
            val now = Clock.System.now()
            aulaIds.forEachIndexed { index, idStr ->
                val uuid = UUID.fromString(idStr)
                val aula = AulaEntity.findById(uuid) ?: return@forEachIndexed
                if (aula.modulo.id != modulo.id) {
                    throw ValidationException("Aula não pertence ao módulo", "aulaIds")
                }
                aula.ordem = index + 1
                aula.dtUpd = now
                aula.idUserUpd = UUID.fromString(professorId)
            }
            true
        }
    }
    
    fun upsertConteudo(aulaId: String, request: ConteudoUpdateRequest, professorId: String): ConteudoResponse {
        return transaction {
            val aula = AulaEntity.find { (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) }
                .firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para editar o conteúdo desta aula")
            }
            val now = Clock.System.now()
            val existing = ConteudoEntity.find { (Conteudos.aulaId eq aula.id) and (Conteudos.isActive eq true) }
                .firstOrNull()
            val conteudo = if (existing == null) {
                ConteudoEntity.new {
                    urlVideo = request.urlVideo
                    textoMarkdown = request.textoMarkdown
                    arquivoUrl = request.arquivoUrl
                    this.aula = aula
                    dtIns = now
                    dtUpd = now
                    idUserIns = UUID.fromString(professorId)
                    idUserUpd = UUID.fromString(professorId)
                    isActive = true
                }
            } else {
                request.urlVideo?.let { existing.urlVideo = it }
                request.textoMarkdown?.let { existing.textoMarkdown = it }
                request.arquivoUrl?.let { existing.arquivoUrl = it }
                existing.dtUpd = now
                existing.idUserUpd = UUID.fromString(professorId)
                existing
            }
            
            // Se uma URL de vídeo foi fornecida, extrair e salvar metadados automaticamente
            println("DEBUG: request.urlVideo = ${request.urlVideo}")
            request.urlVideo?.let { videoUrl ->
                println("DEBUG: videoUrl não é null: $videoUrl")
                if (videoUrl.isNotBlank()) {
                    println("DEBUG: videoUrl não está em branco, iniciando extração de metadados")
                    try {
                        val videoMetadataService = VideoMetadataService()
                        val videoMetadata = runBlocking {
                            videoMetadataService.extractVideoMetadataFromUrl(videoUrl)
                        }
                        println("videoMetadata: $videoMetadata")
                        // Atualizar metadados da aula
                        updateAulaVideoMetadata(aulaId, videoMetadata, professorId)
                        println("DEBUG: Metadados atualizados com sucesso")
                    } catch (e: Exception) {
                        // Se falhar a extração de metadados, continua normalmente
                        // Log do erro seria ideal aqui
                        println("Erro ao extrair metadados do vídeo: ${e.message}")
                        e.printStackTrace()
                    }
                } else {
                    println("DEBUG: videoUrl está em branco")
                }
            } ?: println("DEBUG: request.urlVideo é null")
            
            ConteudoResponse(
                id = conteudo.id.value.toString(),
                urlVideo = conteudo.urlVideo,
                textoMarkdown = conteudo.textoMarkdown,
                arquivoUrl = conteudo.arquivoUrl,
                aulaId = aula.id.value.toString()
            )
        }
    }
    
    // ========== METADADOS DE VÍDEO ==========
    
    fun updateAulaVideoMetadata(aulaId: String, videoMetadata: VideoMetadata, professorId: String): Boolean {
        return transaction {
            val aula = AulaEntity.find { (Aulas.id eq UUID.fromString(aulaId)) and (Aulas.isActive eq true) }
                .firstOrNull() ?: throw NotFoundException("Aula não encontrada")
            
            if (aula.modulo.produto.professor.id.value.toString() != professorId) {
                throw ForbiddenException("Você não tem permissão para editar esta aula")
            }
            
            val now = Clock.System.now()
            
            // Atualizar metadados do vídeo
            aula.videoDuracaoSegundos = videoMetadata.duracaoSegundos
            aula.videoResolucao = videoMetadata.resolucao
            aula.videoTamanhoBytes = videoMetadata.tamanhoBytes
            aula.videoCodec = videoMetadata.codec
            aula.videoFps = videoMetadata.fps
            aula.videoAspectRatio = videoMetadata.aspectRatio
            
            aula.dtUpd = now
            aula.idUserUpd = UUID.fromString(professorId)
            
            true
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
            if (valor < BigDecimal.ZERO) {
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
