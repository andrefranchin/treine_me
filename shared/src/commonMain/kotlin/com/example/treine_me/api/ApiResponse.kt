package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
	val message: String,
	val details: String? = null,
	val field: String? = null
)

@Serializable
data class ApiResponse<T>(
	val success: Boolean,
	val data: T? = null,
	val error: ApiError? = null
)


