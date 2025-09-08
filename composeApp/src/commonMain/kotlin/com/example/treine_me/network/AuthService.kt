package com.example.treine_me.network

import com.example.treine_me.api.AlunoCreateRequest
import com.example.treine_me.api.AlunoResponse
import com.example.treine_me.api.LoginRequest
import com.example.treine_me.api.LoginResponse
import com.example.treine_me.api.ProfessorCreateRequest
import com.example.treine_me.api.ProfessorResponse

class AuthService {

	suspend fun login(email: String, senha: String): LoginResponse {
		val loginResponse = ApiClient.post<LoginResponse>(
			path = "/auth/login",
			body = LoginRequest(email, senha)
		)
		TokenStore.token = loginResponse.token
		return loginResponse
	}

	suspend fun loginProfessor(email: String, senha: String): LoginResponse {
		val loginResponse = ApiClient.post<LoginResponse>(
			path = "/auth/professor/login",
			body = LoginRequest(email, senha)
		)
		println("PROFESSOR$loginResponse")

		TokenStore.token = loginResponse.token
		return loginResponse
	}

	suspend fun registerProfessor(req: ProfessorCreateRequest): ProfessorResponse {
		return ApiClient.post(
			path = "/auth/register/professor",
			body = req
		)
	}

	suspend fun registerAluno(req: AlunoCreateRequest): AlunoResponse {
		return ApiClient.post(
			path = "/auth/register/aluno",
			body = req
		)
	}
}


