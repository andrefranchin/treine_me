package com.example.treine_me

enum class AppRole {
    ALUNO,
    PROFESSOR,
    ADMIN
}

expect fun defaultAppRole(): AppRole


