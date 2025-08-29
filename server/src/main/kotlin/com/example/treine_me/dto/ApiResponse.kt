package com.example.treine_me.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(
            success = true,
            data = data
        )
        
        fun error(message: String, details: String? = null, field: String? = null): ApiResponse<Unit> =
            ApiResponse(
                success = false,
                data = null,
                error = ApiError(message, details, field)
            )
    }
}

@Serializable
data class ApiError(
    val message: String,
    val details: String? = null,
    val field: String? = null
)
