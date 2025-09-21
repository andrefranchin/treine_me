package com.example.treine_me.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.partialcontent.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
        anyHost() // Para desenvolvimento - em produção, especificar hosts específicos
    }
    
    // Suporte para arquivos grandes
    install(PartialContent) {
        // Maximum number of ranges that will be accepted from a HTTP request.
        maxRangeCount = 10
    }
}
