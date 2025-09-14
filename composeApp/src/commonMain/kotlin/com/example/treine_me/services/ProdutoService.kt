package com.example.treine_me.services

import com.example.treine_me.api.ApiError
import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.PaginatedResponse
import com.example.treine_me.api.ProdutoCreateRequest
import com.example.treine_me.api.ProdutoResponse
import com.example.treine_me.api.ProdutoUpdateRequest
import com.example.treine_me.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ProdutoService {
    private val client = ApiClient.client

    suspend fun createProduto(request: ProdutoCreateRequest): ApiResponse<ProdutoResponse> {
        return try {
            val response = client.post("/professores/me/produtos") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao criar produto: ${e.message}"))
        }
    }

    suspend fun listProdutos(page: Int = 1, size: Int = 20): ApiResponse<PaginatedResponse<ProdutoResponse>> {
        return try {
            val response = client.get("/professores/me/produtos?page=$page&size=$size")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao listar produtos: ${e.message}"))
        }
    }

    suspend fun getProduto(id: String): ApiResponse<ProdutoResponse> {
        return try {
            val response = client.get("/professores/me/produtos/$id")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao buscar produto: ${e.message}"))
        }
    }

    suspend fun updateProduto(id: String, request: ProdutoUpdateRequest): ApiResponse<ProdutoResponse> {
        return try {
            val response = client.put("/professores/me/produtos/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao atualizar produto: ${e.message}"))
        }
    }

    suspend fun deleteProduto(id: String): ApiResponse<Map<String, Boolean>> {
        return try {
            val response = client.delete("/professores/me/produtos/$id")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao deletar produto: ${e.message}"))
        }
    }
}


