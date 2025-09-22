package com.example.treine_me.repository

import com.example.treine_me.api.*
import com.example.treine_me.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class PublicRepository {
    private val client = ApiClient.client
    
    suspend fun getProfessor(professorId: String): ApiResponse<ProfessorResponse> {
        return try {
            client.get("/public/professor/$professorId").body<ApiResponse<ProfessorResponse>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar professor: ${e.message}"))
        }
    }
    
    suspend fun getPlanos(professorId: String, page: Int = 1, size: Int = 20): ApiResponse<PaginatedResponse<PlanoResponse>> {
        return try {
            client.get("/public/professor/$professorId/planos") {
                parameter("page", page)
                parameter("size", size)
            }.body<ApiResponse<PaginatedResponse<PlanoResponse>>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar planos: ${e.message}"))
        }
    }
    
    suspend fun getPlano(professorId: String, planoId: String): ApiResponse<PlanoResponse> {
        return try {
            client.get("/public/professor/$professorId/planos/$planoId").body<ApiResponse<PlanoResponse>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar plano: ${e.message}"))
        }
    }
    
    suspend fun getProdutos(professorId: String, page: Int = 1, size: Int = 20): ApiResponse<PaginatedResponse<ProdutoResponse>> {
        return try {
            client.get("/public/professor/$professorId/produtos") {
                parameter("page", page)
                parameter("size", size)
            }.body<ApiResponse<PaginatedResponse<ProdutoResponse>>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar produtos: ${e.message}"))
        }
    }
    
    suspend fun getProduto(professorId: String, produtoId: String): ApiResponse<ProdutoResponse> {
        return try {
            client.get("/public/professor/$professorId/produtos/$produtoId").body<ApiResponse<ProdutoResponse>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar produto: ${e.message}"))
        }
    }
    
    suspend fun getProdutosByPlano(professorId: String, planoId: String): ApiResponse<List<ProdutoResponse>> {
        return try {
            client.get("/public/professor/$professorId/planos/$planoId/produtos").body<ApiResponse<List<ProdutoResponse>>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar produtos do plano: ${e.message}"))
        }
    }
    
    suspend fun getModulosByProduto(professorId: String, produtoId: String): ApiResponse<List<ModuloResponse>> {
        return try {
            client.get("/public/professor/$professorId/produtos/$produtoId/modulos").body<ApiResponse<List<ModuloResponse>>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar módulos: ${e.message}"))
        }
    }
    
    suspend fun getModulo(professorId: String, moduloId: String): ApiResponse<ModuloResponse> {
        return try {
            client.get("/public/professor/$professorId/modulos/$moduloId").body<ApiResponse<ModuloResponse>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar módulo: ${e.message}"))
        }
    }
    
    suspend fun getAulasByModulo(professorId: String, moduloId: String): ApiResponse<List<PublicAulaResponse>> {
        return try {
            client.get("/public/professor/$professorId/modulos/$moduloId/aulas").body<ApiResponse<List<PublicAulaResponse>>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar aulas: ${e.message}"))
        }
    }
    
    suspend fun getAula(professorId: String, aulaId: String): ApiResponse<PublicAulaResponse> {
        return try {
            client.get("/public/professor/$professorId/aulas/$aulaId").body<ApiResponse<PublicAulaResponse>>()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar aula: ${e.message}"))
        }
    }
}

@kotlinx.serialization.Serializable
data class PublicAulaResponse(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ordem: Int,
    val tipoConteudo: TipoConteudo,
    val planoId: String,
    val moduloId: String,
    val temConteudo: Boolean
)
