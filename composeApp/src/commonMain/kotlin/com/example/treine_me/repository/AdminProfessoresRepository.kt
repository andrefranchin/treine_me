package com.example.treine_me.repository

import com.example.treine_me.api.PaginatedResponse
import com.example.treine_me.api.ProfessorCreateRequest
import com.example.treine_me.api.ProfessorResponse
import com.example.treine_me.api.ProfessorUpdateRequest
import com.example.treine_me.api.ResetPasswordRequest
import com.example.treine_me.network.ApiClient
import com.example.treine_me.storage.Store

class AdminProfessoresRepository(
    private val store: Store<ProfessorResponse>
) {
    suspend fun list(page: Int = 1, size: Int = 20): PaginatedResponse<ProfessorResponse> {
        val res: PaginatedResponse<ProfessorResponse> = ApiClient.get("/admin/professores?page=$page&size=$size")
        store.set(res.data)
        return res
    }

    suspend fun get(id: String, refresh: Boolean = true): ProfessorResponse {
        val prof: ProfessorResponse = ApiClient.get("/admin/professores/$id")
        store.set(prof)
        return prof
    }

    suspend fun create(req: ProfessorCreateRequest): ProfessorResponse {
        val created: ProfessorResponse = ApiClient.post("/admin/professores", req)
        store.set(created)
        return created
    }

    suspend fun update(id: String, req: ProfessorUpdateRequest): ProfessorResponse {
        val updated: ProfessorResponse = ApiClient.put("/admin/professores/$id", req)
        store.set(updated)
        return updated
    }

    suspend fun delete(id: String): Boolean {
        val result: Map<String, Boolean> = ApiClient.delete("/admin/professores/$id")
        if (result["success"] == true) {
            store.remove(listOf(id))
            return true
        }
        return false
    }

    suspend fun resetPassword(id: String, newPassword: String): Boolean {
        val result: Map<String, Boolean> = ApiClient.post(
            "/admin/professores/$id/reset-password",
            ResetPasswordRequest(newPassword)
        )
        return result["success"] == true
    }
}


