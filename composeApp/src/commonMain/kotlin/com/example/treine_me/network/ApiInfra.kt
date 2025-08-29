package com.example.treine_me.network

object ApiConfig {
	// Ajuste conforme ambiente
	var baseUrl: String = "http://localhost:8080"
}

object TokenStore {
	var token: String? = null
}

class ApiException(
	override val message: String,
	val details: String? = null,
	val field: String? = null
) : Exception(message)


