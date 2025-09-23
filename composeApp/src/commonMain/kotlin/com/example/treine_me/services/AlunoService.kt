package com.example.treine_me.services

import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.PaginatedResponse
import com.example.treine_me.api.AlunoCreateRequest
import com.example.treine_me.api.AlunoResponse
import com.example.treine_me.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class AlunoUpdateRequest(
    val nome: String? = null,
    val fotoPerfilUrl: String? = null
)

@Serializable
data class AlunoComInscricoesResponse(
    val id: String,
    val nome: String,
    val email: String,
    val fotoPerfilUrl: String? = null,
    val inscricoesAtivas: List<InscricaoResponse>
)

@OptIn(kotlin.time.ExperimentalTime::class)
@Serializable
data class InscricaoResponse(
    val id: String,
    @kotlinx.serialization.Contextual
    val dtInicio: Instant,
    @kotlinx.serialization.Contextual
    val dtFim: Instant? = null,
    val status: com.example.treine_me.api.StatusInscricao,
    val aluno: AlunoResponse,
    val plano: PlanoResponse
)

@Serializable
data class PlanoResponse(
    val id: String,
    val nome: String,
    val descricao: String,
    val valor: String,
    val recorrencia: com.example.treine_me.api.Recorrencia,
    val professorId: String,
    val professorNome: String
)

@Serializable
data class ResetAlunoPasswordRequest(
    val newPassword: String
)

class AlunoService {
    private val client = ApiClient.client

    suspend fun createAluno(request: AlunoCreateRequest): ApiResponse<AlunoResponse> {
        return try {
            val response = client.post("/professores/me/alunos") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao criar aluno: ${e.message}"))
        }
    }

    suspend fun listAlunos(page: Int = 1, size: Int = 20): ApiResponse<PaginatedResponse<AlunoComInscricoesResponse>> {
        return try {
            val response = client.get("/professores/me/alunos?page=$page&size=$size")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao listar alunos: ${e.message}"))
        }
    }

    suspend fun getAluno(id: String): ApiResponse<AlunoComInscricoesResponse> {
        return try {
            val response = client.get("/professores/me/alunos/$id")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao buscar aluno: ${e.message}"))
        }
    }

    suspend fun updateAluno(id: String, request: AlunoUpdateRequest): ApiResponse<AlunoResponse> {
        return try {
            val response = client.put("/professores/me/alunos/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao atualizar aluno: ${e.message}"))
        }
    }

    suspend fun deactivateAluno(id: String): ApiResponse<Boolean> {
        return try {
            val response = client.delete("/professores/me/alunos/$id")
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao desativar aluno: ${e.message}"))
        }
    }

    suspend fun resetAlunoPassword(id: String, request: ResetAlunoPasswordRequest): ApiResponse<Boolean> {
        return try {
            val response = client.post("/professores/me/alunos/$id/reset-password") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(false, null, com.example.treine_me.api.ApiError("Erro ao resetar senha: ${e.message}"))
        }
    }
}
