package com.example.treine_me.services

import com.example.treine_me.api.ApiError
import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.AulaCreateRequest
import com.example.treine_me.api.AulaResponse
import com.example.treine_me.api.AulaUpdateRequest
import com.example.treine_me.api.ConteudoResponse
import com.example.treine_me.api.ConteudoUpdateRequest
import com.example.treine_me.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AulaService {
    private val client = ApiClient.client

    suspend fun listAulas(produtoId: String, moduloId: String): ApiResponse<List<AulaResponse>> {
        return try {
            val response = client.get("/professores/me/produtos/$produtoId/modulos/$moduloId/aulas")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao listar aulas: ${e.message}"))
        }
    }

    suspend fun createAula(produtoId: String, moduloId: String, request: AulaCreateRequest): ApiResponse<AulaResponse> {
        return try {
            val response = client.post("/professores/me/produtos/$produtoId/modulos/$moduloId/aulas") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao criar aula: ${e.message}"))
        }
    }

    suspend fun updateAula(produtoId: String, moduloId: String, aulaId: String, request: AulaUpdateRequest): ApiResponse<AulaResponse> {
        return try {
            val response = client.put("/professores/me/produtos/$produtoId/modulos/$moduloId/aulas/$aulaId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao atualizar aula: ${e.message}"))
        }
    }

    suspend fun deleteAula(produtoId: String, moduloId: String, aulaId: String): ApiResponse<Map<String, Boolean>> {
        return try {
            val response = client.delete("/professores/me/produtos/$produtoId/modulos/$moduloId/aulas/$aulaId")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao deletar aula: ${e.message}"))
        }
    }

    suspend fun reorderAulas(produtoId: String, moduloId: String, aulaIds: List<String>): ApiResponse<Map<String, Boolean>> {
        return try {
            val response = client.put("/professores/me/produtos/$produtoId/modulos/$moduloId/aulas/reorder") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("aulaIds" to aulaIds))
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao reordenar aulas: ${e.message}"))
        }
    }

    suspend fun upsertConteudo(produtoId: String, moduloId: String, aulaId: String, request: ConteudoUpdateRequest): ApiResponse<ConteudoResponse> {
        return try {
            val response = client.put("/professores/me/produtos/$produtoId/modulos/$moduloId/aulas/$aulaId/conteudo") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao salvar conteúdo: ${e.message}"))
        }
    }

    suspend fun getAula(produtoId: String, moduloId: String, aulaId: String): ApiResponse<AulaResponse> {
        return try {
            val response = client.get("/professores/me/produtos/$produtoId/modulos/$moduloId/aulas/$aulaId")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao carregar aula: ${e.message}"))
        }
    }
}


