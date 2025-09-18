package com.example.treine_me.services

import com.example.treine_me.api.ApiError
import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.ModuloCreateRequest
import com.example.treine_me.api.ModuloResponse
import com.example.treine_me.api.ModuloUpdateRequest
import com.example.treine_me.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ModuloService {
    private val client = ApiClient.client

    suspend fun listModulos(produtoId: String): ApiResponse<List<ModuloResponse>> {
        return try {
            val response = client.get("/professores/me/produtos/$produtoId/modulos")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao listar módulos: ${e.message}"))
        }
    }

    suspend fun createModulo(produtoId: String, request: ModuloCreateRequest): ApiResponse<ModuloResponse> {
        return try {
            val response = client.post("/professores/me/produtos/$produtoId/modulos") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao criar módulo: ${e.message}"))
        }
    }

    suspend fun updateModulo(produtoId: String, moduloId: String, request: ModuloUpdateRequest): ApiResponse<ModuloResponse> {
        return try {
            val response = client.put("/professores/me/produtos/$produtoId/modulos/$moduloId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao atualizar módulo: ${e.message}"))
        }
    }

    suspend fun deleteModulo(produtoId: String, moduloId: String): ApiResponse<Map<String, Boolean>> {
        return try {
            val response = client.delete("/professores/me/produtos/$produtoId/modulos/$moduloId")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao deletar módulo: ${e.message}"))
        }
    }
}


