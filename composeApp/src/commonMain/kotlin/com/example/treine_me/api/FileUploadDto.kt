package com.example.treine_me.api

import kotlinx.serialization.Serializable

@Serializable
data class FileUploadResponse(
    val fileName: String,
    val url: String,
    val contentType: String,
    val size: Long
)
