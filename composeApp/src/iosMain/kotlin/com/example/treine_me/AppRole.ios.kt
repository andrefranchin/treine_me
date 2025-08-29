package com.example.treine_me

import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo

actual fun defaultAppRole(): AppRole {
    // 1) CLI arg: look for "-APP_ROLE admin" or "-APP_ROLE=admin" or "APP_ROLE=admin"
    val argsList = NSProcessInfo.processInfo.arguments?.map { it.toString() } ?: emptyList()
    val argValueInline = argsList.firstOrNull { it.startsWith("-APP_ROLE=") || it.startsWith("APP_ROLE=") }
        ?.substringAfter("=")
        ?.lowercase()
    val argValueSplit = run {
        val idx = argsList.indexOf("-APP_ROLE")
        if (idx >= 0 && idx + 1 < argsList.size) argsList[idx + 1].lowercase() else null
    }
    val cliRole = argValueInline ?: argValueSplit

    // 2) Env var from scheme
    val envRole = (NSProcessInfo.processInfo.environment["APP_ROLE"] as? String)?.lowercase()

    // 3) Info.plist fallback
    val plistRole = (NSBundle.mainBundle.objectForInfoDictionaryKey("APP_ROLE") as? String)?.lowercase()

    val role = cliRole ?: envRole ?: plistRole
    return when (role) {
        "aluno" -> AppRole.ALUNO
        "professor" -> AppRole.PROFESSOR
        "admin" -> AppRole.ADMIN
        else -> AppRole.PROFESSOR
    }
}


