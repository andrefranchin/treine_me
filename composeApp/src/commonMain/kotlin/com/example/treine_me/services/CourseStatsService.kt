package com.example.treine_me.services

import com.example.treine_me.api.ApiError
import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.ModuloResponse
import com.example.treine_me.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get

data class CourseStats(
    val moduleCount: Int,
    val lessonCount: Int
)

class CourseStatsService {
    private val client = ApiClient.client

    suspend fun getCourseStats(produtoId: String): ApiResponse<CourseStats> {
        return try {
            val response = client.get("/professores/me/produtos/$produtoId/modulos")
            val modulosResponse: ApiResponse<List<ModuloResponse>> = response.body()
            
            if (modulosResponse.success && modulosResponse.data != null) {
                val modulos = modulosResponse.data!!
                val moduleCount = modulos.size
                val lessonCount = modulos.sumOf { it.aulas.size }
                
                ApiResponse(true, CourseStats(moduleCount, lessonCount), null)
            } else {
                ApiResponse(false, null, modulosResponse.error)
            }
        } catch (e: Exception) {
            ApiResponse(false, null, ApiError("Erro ao carregar estatísticas: ${e.message}"))
        }
    }
}
