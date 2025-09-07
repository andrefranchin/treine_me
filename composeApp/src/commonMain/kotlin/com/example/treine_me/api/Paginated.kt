package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
    val totalPages: Long
)


