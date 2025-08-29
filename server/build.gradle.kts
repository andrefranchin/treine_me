plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
    application
}

group = "com.example.treine_me"
version = "1.0.0"
application {
    mainClass.set("com.example.treine_me.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    
    // Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-content-negotiation:${libs.versions.ktor.get()}")
    
    // Authentication & JWT
    implementation("io.ktor:ktor-server-auth:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-auth-jwt:${libs.versions.ktor.get()}")
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.57.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.57.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.57.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.57.0")
    implementation("com.h2database:h2:2.2.224") // Para desenvolvimento
    implementation("org.postgresql:postgresql:42.7.3") // Para produção
    
    // Validation
    implementation("io.ktor:ktor-server-request-validation:${libs.versions.ktor.get()}")
    
    // CORS
    implementation("io.ktor:ktor-server-cors:${libs.versions.ktor.get()}")
    
    // Status Pages (Exception Handling)
    implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
    
    // OpenAPI/Swagger Documentation
    implementation("io.ktor:ktor-server-openapi:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-swagger:${libs.versions.ktor.get()}")
    
    // Password Hashing
    implementation("org.mindrot:jbcrypt:0.4")
    
    // DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation(libs.protolite.well.known.types)
    
    // HTTP Client para Cloudflare R2
    implementation("io.ktor:ktor-client-core:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-client-cio:${libs.versions.ktor.get()}")
    
    // File Upload
    implementation("io.ktor:ktor-server-partial-content:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-auto-head-response:${libs.versions.ktor.get()}")

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}