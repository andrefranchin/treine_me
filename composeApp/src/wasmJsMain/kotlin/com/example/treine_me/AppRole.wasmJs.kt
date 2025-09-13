package com.example.treine_me

import kotlinx.browser.window

actual fun defaultAppRole(): AppRole {
    val role = runCatching {
        val query = window.location.search.removePrefix("?")
        val pairs = query.split("&").filter { it.isNotBlank() }
        val map = pairs.mapNotNull { pair ->
            val idx = pair.indexOf('=')
            if (idx <= 0) null else pair.substring(0, idx) to pair.substring(idx + 1)
        }.toMap()
        (map["APP_ROLE"] ?: map["app_role"])?.lowercase()
    }.getOrNull()

    return when (role) {
        "aluno" -> AppRole.ALUNO
        "professor" -> AppRole.PROFESSOR
        "admin" -> AppRole.ADMIN
        else -> AppRole.PROFESSOR
    }
}



