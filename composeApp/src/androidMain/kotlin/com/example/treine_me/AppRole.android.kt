package com.example.treine_me

actual fun defaultAppRole(): AppRole = when (BuildConfig.FLAVOR) {
    "aluno" -> AppRole.ALUNO
    "professor" -> AppRole.PROFESSOR
    "admin" -> AppRole.ADMIN
    else -> AppRole.PROFESSOR
}


