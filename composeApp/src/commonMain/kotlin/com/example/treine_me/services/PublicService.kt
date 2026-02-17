package com.example.treine_me.services

import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.PaginatedResponse
import com.example.treine_me.api.ProdutoResponse
import com.example.treine_me.api.PlanoResponse
import com.example.treine_me.api.ModuloResponse
import com.example.treine_me.api.PublicAulaResponse
import com.example.treine_me.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers

/**
 * Serviço para acessar APIs públicas que não requerem autenticação.
 * Usado para mostrar conteúdo para usuários não logados.
 */
class PublicService {
    private val client = ApiClient.client

    suspend fun listProdutos(professorId: String, page: Int = 1, size: Int = 20): ApiResponse<PaginatedResponse<ProdutoResponse>> {
        return try {
            val response = client.get("/public/professor/$professorId/produtos?page=$page&size=$size")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao listar produtos públicos: ${e.message}"))
        }
    }

    suspend fun getProduto(produtoId: String, professorId: String): ApiResponse<ProdutoResponse> {
        return try {
            val response = client.get("/public/professor/$professorId/produtos/$produtoId")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao buscar produto público: ${e.message}"))
        }
    }

    suspend fun listPlanos(professorId: String, page: Int = 1, size: Int = 20): ApiResponse<PaginatedResponse<PlanoResponse>> {
        return try {
            val response = client.get("/public/professor/$professorId/planos?page=$page&size=$size")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao listar planos públicos: ${e.message}"))
        }
    }

    suspend fun listModulosByProduto(produtoId: String, professorId: String, token: String? = null): ApiResponse<List<ModuloResponse>> {
        return try {
            val response = client.get("/public/professor/$professorId/produtos/$produtoId/modulos") {
                token?.let {
                    headers {
                        append("Authorization", if (it.startsWith("Bearer ")) it else "Bearer $it")
                    }
                }
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao listar módulos públicos: ${e.message}"))
        }
    }

    suspend fun listAulasByModulo(moduloId: String, professorId: String, token: String? = null): ApiResponse<List<PublicAulaResponse>> {
        return try {
            val response = client.get("/public/professor/$professorId/modulos/$moduloId/aulas") {
                token?.let {
                    headers {
                        append("Authorization", if (it.startsWith("Bearer ")) it else "Bearer $it")
                    }
                }
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao listar aulas públicas: ${e.message}"))
        }
    }
}
