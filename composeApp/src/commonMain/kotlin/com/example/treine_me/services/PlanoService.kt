package com.example.treine_me.services

import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.PlanoCreateRequest
import com.example.treine_me.api.PlanoResponse
import com.example.treine_me.api.PlanoUpdateRequest
import com.example.treine_me.api.PaginatedResponse
import com.example.treine_me.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PlanoService {
    private val client = ApiClient.client
    
    suspend fun createPlano(request: PlanoCreateRequest): ApiResponse<PlanoResponse> {
        return try {
            val response = client.post("/professores/me/planos") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body<ApiResponse<PlanoResponse>>()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao criar plano: ${e.message}"))
        }
    }
    
    suspend fun listPlanos(page: Int = 1, size: Int = 20): ApiResponse<PaginatedResponse<PlanoResponse>> {
        return try {
            val response = client.get("/professores/me/planos?page=$page&size=$size")
            response.body<ApiResponse<PaginatedResponse<PlanoResponse>>>()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao listar planos: ${e.message}"))
        }
    }
    
    suspend fun getPlano(id: String): ApiResponse<PlanoResponse> {
        return try {
            val response = client.get("/professores/me/planos/$id")
            response.body<ApiResponse<PlanoResponse>>()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao buscar plano: ${e.message}"))
        }
    }
    
    suspend fun updatePlano(id: String, request: PlanoUpdateRequest): ApiResponse<PlanoResponse> {
        return try {
            val response = client.put("/professores/me/planos/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body<ApiResponse<PlanoResponse>>()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao atualizar plano: ${e.message}"))
        }
    }
    
    suspend fun deletePlano(id: String): ApiResponse<Map<String, Boolean>> {
        return try {
            val response = client.delete("/professores/me/planos/$id")
            response.body<ApiResponse<Map<String, Boolean>>>()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao deletar plano: ${e.message}"))
        }
    }
}
