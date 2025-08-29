package com.example.treine_me

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform