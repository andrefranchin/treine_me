package com.example.treine_me.services

import com.example.treine_me.api.ApiResponse
import com.example.treine_me.api.LoginRequest
import com.example.treine_me.api.LoginResponse
import com.example.treine_me.config.AlunoConfig
import com.example.treine_me.network.ApiClient
import com.example.treine_me.network.TokenStore
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

/**
 * Serviço de autenticação específico para o app do aluno.
 * Gerencia o login e token de sessão do aluno.
 */
class AlunoAuthService {
    private val client = ApiClient.client

    /**
     * Realiza o login do aluno usando o professorId configurado.
     */
    suspend fun login(email: String, senha: String): ApiResponse<LoginResponse> {
        return try {
            val request = AlunoLoginRequest(
                email = email,
                senha = senha,
                professorId = AlunoConfig.professorId
            )
            
            val response = client.post("/auth/aluno/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            val loginResponse = response.body<ApiResponse<LoginResponse>>()
            
            // Se o login foi bem-sucedido, armazenar o token
            if (loginResponse.success) {
                loginResponse.data?.let { data ->
                    TokenStore.token = data.token
                }
            }
            
            loginResponse
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                data = null,
                error = com.example.treine_me.api.ApiError("Erro ao fazer login: ${e.message}")
            )
        }
    }

    /**
     * Realiza o logout limpando o token armazenado.
     */
    fun logout() {
        TokenStore.token = null
    }

    /**
     * Verifica se o usuário está logado (possui token).
     */
    fun isLoggedIn(): Boolean {
        return TokenStore.token != null
    }

    /**
     * Obtém o token atual do usuário.
     */
    fun getCurrentToken(): String? {
        return TokenStore.token
    }
}

/**
 * DTO para requisição de login do aluno.
 */
@Serializable
data class AlunoLoginRequest(
    val email: String,
    val senha: String,
    val professorId: String
)
