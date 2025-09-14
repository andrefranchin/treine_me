package com.example.treine_me.storage

import com.example.treine_me.Constants
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class CloudflareR2Service : StorageService {
    
    private val client = HttpClient(CIO)
    
    // Configurações do R2 - hardcoded a pedido do usuário
    private val accountId = "24be5a76d99e172619714a8eb94b63d9"
    private val baseUrl = "https://$accountId.r2.cloudflarestorage.com"
    private val bucketName = "treine-me"
    private val publicUrl = "https://pub-c6a5b3601e18469b8fd1c2631bb528f7.r2.dev"
    private val accessKeyId = "d29b08824781d62ae302c1cce0dc973c"
    private val secretAccessKey = "334c44d6c02c4fe47cc572b8131c2e37df42a760aec011d8990db32bab6fd243"
    private val region = "auto" // R2 usa "auto" como região padrão
    
    override suspend fun uploadFile(
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        folder: String
    ): UploadResult = withContext(Dispatchers.IO) {
        try {
            // Para R2 (path-style), a URL deve incluir o bucket no caminho
            val key = if (folder.isNotEmpty()) "$bucketName/$folder/$fileName" else "$bucketName/$fileName"
            val url = "$baseUrl/$key"
            
            val timestamp = Instant.now().atOffset(ZoneOffset.UTC)
            val dateStamp = timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val amzDate = timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"))
            
            val fileBytes = inputStream.readBytes()
            
            val headers = mutableMapOf<String, String>()
            headers["Host"] = "$accountId.r2.cloudflarestorage.com"
            headers["Content-Type"] = contentType
            headers["Content-Length"] = fileBytes.size.toString()
            headers["X-Amz-Date"] = amzDate
            headers["X-Amz-Content-Sha256"] = sha256Hash(fileBytes)
            
            val authHeader = generateAuthHeader("PUT", key, headers, dateStamp, amzDate)
            headers["Authorization"] = authHeader
            
            val response = client.put(url) {
                headers.forEach { (name, value) ->
                    header(name, value)
                }
                setBody(fileBytes)
            }
            
            if (response.status.isSuccess()) {
                UploadResult(
                    success = true,
                    fileName = fileName,
                    // Retornar URL pública para consumo no app
                    url = "$publicUrl/" + key.removePrefix("$bucketName/")
                )
            } else {
                val body = response.bodyAsText()
                val status = response.status
                val reason = status.description
                val detailed = buildString {
                    append("Erro no upload: ")
                    append(status.value).append(" ").append(reason)
                    if (body.isNotBlank()) {
                        append(" | body: ").append(body.take(500))
                    }
                }
                UploadResult(
                    success = false,
                    fileName = fileName,
                    url = "",
                    message = detailed
                )
            }
        } catch (e: Exception) {
            UploadResult(
                success = false,
                fileName = fileName,
                url = "",
                message = "Erro no upload: ${e.message}"
            )
        }
    }
    
    override suspend fun deleteFile(fileName: String, folder: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val key = if (folder.isNotEmpty()) "$bucketName/$folder/$fileName" else "$bucketName/$fileName"
            val url = "$baseUrl/$key"
            
            val timestamp = Instant.now().atOffset(ZoneOffset.UTC)
            val dateStamp = timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val amzDate = timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"))
            
            val headers = mutableMapOf<String, String>()
            headers["Host"] = "$accountId.r2.cloudflarestorage.com"
            headers["X-Amz-Date"] = amzDate
            headers["X-Amz-Content-Sha256"] = sha256Hash("")
            
            val authHeader = generateAuthHeader("DELETE", key, headers, dateStamp, amzDate)
            headers["Authorization"] = authHeader
            
            val response = client.delete(url) {
                headers.forEach { (name, value) ->
                    header(name, value)
                }
            }
            
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun getFileUrl(fileName: String, folder: String): String {
        val key = if (folder.isNotEmpty()) "$folder/$fileName" else fileName
        return "$publicUrl/$key"
    }
    
    override suspend fun generatePresignedUrl(
        fileName: String,
        folder: String,
        expirationMinutes: Int
    ): String {
        // Para R2, vamos usar URLs diretas por enquanto
        // Em um cenário real, implementaríamos presigned URLs com expiração
        return getFileUrl(fileName, folder)
    }
    
    private fun generateAuthHeader(
        method: String,
        key: String,
        headers: Map<String, String>,
        dateStamp: String,
        amzDate: String
    ): String {
        val algorithm = "AWS4-HMAC-SHA256"
        val credentialScope = "$dateStamp/$region/s3/aws4_request"
        
        // Criar canonical request
        val canonicalUri = "/$key"
        val canonicalQuerystring = ""
        val canonicalHeaders = headers.toSortedMap().map { "${it.key.lowercase()}:${it.value}" }.joinToString("\n") + "\n"
        val signedHeaders = headers.keys.map { it.lowercase() }.sorted().joinToString(";")
        val payloadHash = headers["X-Amz-Content-Sha256"] ?: ""
        
        val canonicalRequest = "$method\n$canonicalUri\n$canonicalQuerystring\n$canonicalHeaders\n$signedHeaders\n$payloadHash"
        
        // Criar string para assinar
        val stringToSign = "$algorithm\n$amzDate\n$credentialScope\n${sha256Hash(canonicalRequest)}"
        
        // Calcular assinatura
        val signingKey = getSignatureKey(secretAccessKey, dateStamp, region, "s3")
        val signature = hmacSha256(stringToSign, signingKey).joinToString("") { "%02x".format(it) }
        
        return "$algorithm Credential=$accessKeyId/$credentialScope, SignedHeaders=$signedHeaders, Signature=$signature"
    }
    
    private fun sha256Hash(input: String): String = sha256Hash(input.toByteArray())
    
    private fun sha256Hash(input: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input).joinToString("") { "%02x".format(it) }
    }
    
    private fun hmacSha256(data: String, key: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data.toByteArray())
    }
    
    private fun getSignatureKey(key: String, dateStamp: String, regionName: String, serviceName: String): ByteArray {
        val kDate = hmacSha256(dateStamp, "AWS4$key".toByteArray())
        val kRegion = hmacSha256(regionName, kDate)
        val kService = hmacSha256(serviceName, kRegion)
        return hmacSha256("aws4_request", kService)
    }
}
