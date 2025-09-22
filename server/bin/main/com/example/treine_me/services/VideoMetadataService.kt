package com.example.treine_me.services

import kotlinx.serialization.Serializable
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.sax.BodyContentHandler
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.ByteArrayInputStream
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFprobe
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Serviço para extrair metadados de vídeos.
 * 
 * Por enquanto implementa uma versão básica que pode ser expandida
 * para usar bibliotecas como FFmpeg ou outras ferramentas de análise de vídeo.
 */
class VideoMetadataService {
    
    fun extractVideoMetadata(
        fileName: String,
        contentType: String,
        @Suppress("UNUSED_PARAMETER") inputStream: InputStream,
        fileSizeBytes: Long
    ): VideoMetadata {
        // Por enquanto, implementação básica que extrai informações do contentType e nome do arquivo
        // Em uma implementação real, você usaria bibliotecas como FFmpeg, MediaInfo, etc.
        
        val resolucao = extractResolutionFromFileName(fileName)
        val codec = extractCodecFromContentType(contentType)
        val aspectRatio = getAspectRatioFromResolution(resolucao)
        val fps = extractFpsFromFileName(fileName)
        
        return VideoMetadata(
            duracaoSegundos = null, // Seria extraído via FFmpeg ou similar
            resolucao = resolucao ?: "720p", // Valor padrão se não conseguir detectar
            tamanhoBytes = fileSizeBytes,
            codec = codec ?: "H.264", // Valor padrão
            fps = fps ?: 30, // Valor padrão
            aspectRatio = aspectRatio ?: "16:9" // Valor padrão
        )
    }
    
    /**
     * Extrai metadados reais de vídeo baixando os primeiros bytes do arquivo.
     * Usado quando o vídeo já foi feito upload e só temos a URL.
     */
    suspend fun extractVideoMetadataFromUrl(videoUrl: String): VideoMetadata {
        println("DEBUG VideoMetadataService: Iniciando extração para URL: $videoUrl")
        return try {
            // Primeiro tenta usar FFprobe (mais confiável)
            println("DEBUG VideoMetadataService: Tentando extração com FFprobe")
            val metadata = extractVideoMetadataWithFFprobe(videoUrl)
            println("DEBUG VideoMetadataService: Extração com FFprobe bem-sucedida: $metadata")
            metadata
        } catch (e: Exception) {
            println("DEBUG VideoMetadataService: FFprobe falhou (${e.message}), tentando extração real")
            try {
                // Fallback: tenta extrair metadados reais baixando parte do arquivo
                val metadata = extractRealVideoMetadata(videoUrl)
                println("DEBUG VideoMetadataService: Extração real bem-sucedida: $metadata")
                metadata
            } catch (e2: Exception) {
                // Se falhar, usa a detecção básica pelo nome do arquivo
                println("DEBUG VideoMetadataService: Extração real falhou (${e2.message}), usando extração básica")
                val metadata = extractBasicVideoMetadataFromUrl(videoUrl)
                println("DEBUG VideoMetadataService: Extração básica concluída: $metadata")
                metadata
            }
        }
    }
    
    /**
     * Extrai metadados de vídeo usando FFprobe (mais confiável)
     */
    private suspend fun extractVideoMetadataWithFFprobe(videoUrl: String): VideoMetadata {
        println("DEBUG extractVideoMetadataWithFFprobe: Iniciando para URL: $videoUrl")
        
        // Baixar o vídeo temporariamente para análise local
        val tempFile = downloadVideoTemporarily(videoUrl)
        
        try {
            // Tentar encontrar FFprobe no sistema
            val ffprobe = try {
                FFprobe("/usr/bin/ffprobe") // Caminho comum no Linux/macOS
            } catch (e: Exception) {
                try {
                    FFprobe("/opt/homebrew/bin/ffprobe") // Caminho do Homebrew no macOS
                } catch (e2: Exception) {
                    try {
                        FFprobe("ffprobe") // Tentar no PATH
                    } catch (e3: Exception) {
                        throw Exception("FFprobe não encontrado no sistema. Instale FFmpeg.")
                    }
                }
            }
            
            println("DEBUG extractVideoMetadataWithFFprobe: FFprobe encontrado, analisando arquivo")
            val probeResult = ffprobe.probe(tempFile.absolutePath)
            
            // Extrair informações dos streams
            val videoStream = probeResult.streams.find { it.codec_type?.name == "video" }
            val format = probeResult.format
            
            val duration = format?.duration?.toInt()
            val width = videoStream?.width
            val height = videoStream?.height
            val fps = videoStream?.r_frame_rate?.let { frameRate ->
                // r_frame_rate vem como "30/1" ou "25/1"
                val frameRateStr = frameRate.toString()
                val parts = frameRateStr.split('/')
                if (parts.size == 2) {
                    val numerator = parts[0].toDoubleOrNull() ?: 30.0
                    val denominator = parts[1].toDoubleOrNull() ?: 1.0
                    (numerator / denominator).toInt()
                } else {
                    30
                }
            } ?: 30
            
            val resolucao = if (height != null) {
                when {
                    height >= 2160 -> "2160p"
                    height >= 1440 -> "1440p"
                    height >= 1080 -> "1080p"
                    height >= 720 -> "720p"
                    height >= 480 -> "480p"
                    else -> "360p"
                }
            } else "720p"
            
            val aspectRatio = if (width != null && height != null) {
                val ratio = width.toDouble() / height.toDouble()
                when {
                    ratio > 1.7 -> "16:9"
                    ratio > 1.4 -> "3:2"
                    ratio > 1.2 -> "4:3"
                    else -> "1:1"
                }
            } else "16:9"
            
            val codec = videoStream?.codec_name ?: "H.264"
            val fileSize = tempFile.length()
            
            println("DEBUG extractVideoMetadataWithFFprobe: Metadados extraídos - duration: $duration, resolução: $resolucao, codec: $codec")
            
            return VideoMetadata(
                duracaoSegundos = duration,
                resolucao = resolucao,
                tamanhoBytes = fileSize,
                codec = codec,
                fps = fps,
                aspectRatio = aspectRatio
            )
            
        } finally {
            // Limpar arquivo temporário
            if (tempFile.exists()) {
                tempFile.delete()
                println("DEBUG extractVideoMetadataWithFFprobe: Arquivo temporário removido")
            }
        }
    }
    
    /**
     * Baixa o vídeo temporariamente para análise local
     */
    private suspend fun downloadVideoTemporarily(videoUrl: String): File {
        println("DEBUG downloadVideoTemporarily: Baixando vídeo para análise")
        val client = HttpClient(CIO)
        
        try {
            val response = client.get(videoUrl)
            val contentLength = response.headers["Content-Length"]?.toLongOrNull() ?: 0L
            
            // Limitar download a 50MB para análise
            if (contentLength > 50 * 1024 * 1024) {
                println("DEBUG downloadVideoTemporarily: Arquivo muito grande ($contentLength bytes), baixando apenas início")
                // Para arquivos grandes, baixar apenas os primeiros 50MB
                val limitedResponse = client.get(videoUrl) {
                    headers {
                        append("Range", "bytes=0-${50 * 1024 * 1024 - 1}")
                    }
                }
                val videoBytes = limitedResponse.readRawBytes()
                val tempFile = File.createTempFile("video_analysis_", ".mp4")
                tempFile.writeBytes(videoBytes)
                println("DEBUG downloadVideoTemporarily: Arquivo temporário criado: ${tempFile.absolutePath} (${videoBytes.size} bytes)")
                return tempFile
            } else {
                // Para arquivos pequenos, baixar completo
                val videoBytes = response.readRawBytes()
                val tempFile = File.createTempFile("video_analysis_", ".mp4")
                tempFile.writeBytes(videoBytes)
                println("DEBUG downloadVideoTemporarily: Arquivo temporário criado: ${tempFile.absolutePath} (${videoBytes.size} bytes)")
                return tempFile
            }
        } finally {
            client.close()
        }
    }
    
    /**
     * Estima a duração do vídeo baseado no tamanho do arquivo
     * Esta é uma estimativa grosseira para quando não conseguimos extrair metadados reais
     */
    private suspend fun estimateVideoDuration(@Suppress("UNUSED_PARAMETER") videoUrl: String, fileSizeBytes: Long): Int? {
        return try {
            // Estimativa baseada em bitrates típicos:
            // - Vídeo 720p: ~2-5 Mbps
            // - Vídeo 1080p: ~5-10 Mbps
            // Usando uma média conservadora de 3 Mbps (375 KB/s)
            
            val averageBitrateKBps = 375 // 3 Mbps em KB/s
            val estimatedDuration = (fileSizeBytes / 1024 / averageBitrateKBps).toInt()
            
            // Só retorna se a estimativa for razoável (entre 10 segundos e 2 horas)
            if (estimatedDuration in 10..7200) {
                println("DEBUG estimateVideoDuration: Duração estimada: $estimatedDuration segundos para ${fileSizeBytes} bytes")
                estimatedDuration
            } else {
                println("DEBUG estimateVideoDuration: Estimativa fora do range razoável: $estimatedDuration segundos")
                null
            }
        } catch (e: Exception) {
            println("DEBUG estimateVideoDuration: Erro na estimativa: ${e.message}")
            null
        }
    }
    
    /**
     * Baixa os primeiros bytes do vídeo para extrair metadados reais usando Apache Tika
     */
    private suspend fun extractRealVideoMetadata(videoUrl: String): VideoMetadata {
        println("DEBUG extractRealVideoMetadata: Criando cliente HTTP")
        val client = HttpClient(CIO)
        
        try {
            // Primeiro, vamos tentar obter informações do cabeçalho do arquivo
            println("DEBUG extractRealVideoMetadata: Fazendo requisição HEAD para obter tamanho do arquivo")
            val headResponse = client.head(videoUrl)
            val contentLength = headResponse.headers["Content-Length"]?.toLongOrNull() ?: 0L
            println("DEBUG extractRealVideoMetadata: Tamanho do arquivo: $contentLength bytes")
            
            // Estratégia: baixar início e fim do arquivo para maximizar chances de encontrar metadados
            val chunkSize = minOf(2L * 1024 * 1024, contentLength / 4) // 2MB ou 1/4 do arquivo, o que for menor
            
            println("DEBUG extractRealVideoMetadata: Fazendo requisição HTTP para $videoUrl (primeiros ${chunkSize}bytes)")
            val response = client.get(videoUrl) {
                headers {
                    append("Range", "bytes=0-${chunkSize-1}") // Primeiros 2MB ou 1/4 do arquivo
                }
            }
            println("DEBUG extractRealVideoMetadata: Resposta HTTP recebida, status: ${response.status}")
            
            val videoBytes = response.readRawBytes()
            println("DEBUG extractRealVideoMetadata: Baixados ${videoBytes.size} bytes")
            val inputStream = ByteArrayInputStream(videoBytes)
            
            val metadata = Metadata()
            val parser = AutoDetectParser()
            val handler = BodyContentHandler(-1) // -1 = sem limite
            val parseContext = ParseContext()
            
            // Tentar extrair duração usando parser MP4 customizado primeiro
            var duration: Int? = null
            var width: Int? = null
            var height: Int? = null
            
            try {
                println("DEBUG extractRealVideoMetadata: Tentando parser MP4 customizado")
                val mp4Info = parseMp4Metadata(videoBytes)
                duration = mp4Info.duration
                width = mp4Info.width
                height = mp4Info.height
                println("DEBUG extractRealVideoMetadata: MP4 parser - duration: $duration, width: $width, height: $height")
            } catch (e: Exception) {
                println("DEBUG extractRealVideoMetadata: Parser MP4 falhou: ${e.message}")
            }
            
            // Se o parser MP4 não funcionou, tenta Apache Tika como fallback
            if (duration == null) {
                println("DEBUG extractRealVideoMetadata: Iniciando parse com Apache Tika")
                parser.parse(inputStream, handler, metadata, parseContext)
                println("DEBUG extractRealVideoMetadata: Parse concluído, metadados encontrados: ${metadata.names().toList()}")
                
                duration = extractDurationFromMetadata(metadata)
                if (width == null) width = metadata.get("tiff:ImageWidth")?.toIntOrNull()
                if (height == null) height = metadata.get("tiff:ImageLength")?.toIntOrNull()
                
                println("DEBUG extractRealVideoMetadata: Tika - duration: $duration, width: $width, height: $height")
            }
            
            val frameRate = metadata.get("xmpDM:videoFrameRate")?.toDoubleOrNull()?.toInt()
            
            val resolucao = if (height != null) {
                when {
                    height >= 2160 -> "2160p"
                    height >= 1440 -> "1440p"
                    height >= 1080 -> "1080p"
                    height >= 720 -> "720p"
                    height >= 480 -> "480p"
                    else -> "360p"
                }
            } else {
                extractResolutionFromFileName(videoUrl.substringAfterLast("/")) ?: "720p"
            }
            
            val aspectRatio = if (width != null && height != null) {
                val ratio = width.toDouble() / height.toDouble()
                when {
                    ratio > 1.7 -> "16:9"
                    ratio > 1.4 -> "3:2"
                    ratio > 1.2 -> "4:3"
                    else -> "1:1"
                }
            } else {
                "16:9"
            }
            
            return VideoMetadata(
                duracaoSegundos = duration,
                resolucao = resolucao,
                tamanhoBytes = videoBytes.size.toLong(),
                codec = metadata.get("Content-Type")?.let { extractCodecFromContentType(it) } ?: "H.264",
                fps = frameRate ?: extractFpsFromFileName(videoUrl.substringAfterLast("/")) ?: 30,
                aspectRatio = aspectRatio
            )
            
        } finally {
            client.close()
        }
    }
    
    /**
     * Fallback: extração básica apenas pelo nome do arquivo
     */
    private suspend fun extractBasicVideoMetadataFromUrl(videoUrl: String): VideoMetadata {
        val fileName = videoUrl.substringAfterLast("/")
        val contentType = when {
            fileName.contains(".mp4", ignoreCase = true) -> "video/mp4"
            fileName.contains(".webm", ignoreCase = true) -> "video/webm"
            fileName.contains(".mov", ignoreCase = true) -> "video/quicktime"
            else -> "video/mp4"
        }
        
        val resolucao = extractResolutionFromFileName(fileName)
        val codec = extractCodecFromContentType(contentType)
        val aspectRatio = getAspectRatioFromResolution(resolucao)
        val fps = extractFpsFromFileName(fileName)
        
        // Tentar obter tamanho do arquivo para estimar duração
        var fileSizeBytes = 0L
        var estimatedDuration: Int? = null
        
        try {
            val client = HttpClient(CIO)
            val headResponse = client.head(videoUrl)
            fileSizeBytes = headResponse.headers["Content-Length"]?.toLongOrNull() ?: 0L
            client.close()
            
            if (fileSizeBytes > 0) {
                estimatedDuration = estimateVideoDuration(videoUrl, fileSizeBytes)
            }
        } catch (e: Exception) {
            println("DEBUG extractBasicVideoMetadataFromUrl: Erro ao obter tamanho do arquivo: ${e.message}")
        }
        
        return VideoMetadata(
            duracaoSegundos = estimatedDuration,
            resolucao = resolucao ?: "720p",
            tamanhoBytes = fileSizeBytes,
            codec = codec ?: "H.264",
            fps = fps ?: 30,
            aspectRatio = aspectRatio ?: "16:9"
        )
    }
    
    private fun extractResolutionFromFileName(fileName: String): String? {
        val resolutionPatterns = mapOf(
            "4k" to "2160p",
            "2160" to "2160p",
            "1080" to "1080p",
            "720" to "720p",
            "480" to "480p",
            "360" to "360p",
            "240" to "240p"
        )
        
        val lowerFileName = fileName.lowercase()
        for ((pattern, resolution) in resolutionPatterns) {
            if (lowerFileName.contains(pattern)) {
                return resolution
            }
        }
        
        return null
    }
    
    private fun extractCodecFromContentType(contentType: String): String? {
        return when (contentType) {
            "video/mp4" -> "H.264" // Assumindo H.264 como padrão para MP4
            "video/webm" -> "VP9"
            "video/quicktime" -> "H.264"
            else -> null
        }
    }
    
    private fun getAspectRatioFromResolution(resolution: String?): String? {
        return when (resolution) {
            "2160p", "1080p", "720p", "480p" -> "16:9"
            "360p", "240p" -> "4:3"
            else -> null
        }
    }
    
    private fun extractFpsFromFileName(fileName: String): Int? {
        val fpsPatterns = mapOf(
            "60fps" to 60,
            "30fps" to 30,
            "24fps" to 24,
            "25fps" to 25,
            "50fps" to 50
        )
        
        val lowerFileName = fileName.lowercase()
        for ((pattern, fps) in fpsPatterns) {
            if (lowerFileName.contains(pattern)) {
                return fps
            }
        }
        
        return null
    }
    
    /**
     * Parser básico para extrair metadados de arquivos MP4
     */
    private fun parseMp4Metadata(bytes: ByteArray): Mp4Metadata {
        println("DEBUG parseMp4Metadata: Iniciando parse de ${bytes.size} bytes")
        
        var duration: Int? = null
        var width: Int? = null
        var height: Int? = null
        
        // Buscar recursivamente pelos boxes importantes
        parseBoxes(bytes, 0, bytes.size) { boxType, boxOffset, boxSize ->
            when (boxType) {
                "mvhd" -> {
                    duration = parseMvhdBox(bytes, boxOffset, boxSize)
                }
                "tkhd" -> {
                    val dimensions = parseTkhdBox(bytes, boxOffset, boxSize)
                    if (dimensions.first > 0 && dimensions.second > 0) {
                        width = dimensions.first
                        height = dimensions.second
                    }
                }
            }
        }
        
        return Mp4Metadata(duration, width, height)
    }
    
    /**
     * Parser recursivo para navegar pela estrutura hierárquica do MP4
     */
    private fun parseBoxes(bytes: ByteArray, start: Int, end: Int, onBoxFound: (String, Int, Int) -> Unit) {
        var offset = start
        
        while (offset < end - 8) {
            if (offset + 8 > bytes.size) break
            
            // Ler tamanho do box (4 bytes, big-endian)
            val boxSize = readInt32BigEndian(bytes, offset)
            if (boxSize <= 0 || boxSize > end - offset) break
            
            // Ler tipo do box (4 bytes ASCII)
            val boxType = String(bytes, offset + 4, 4, Charsets.US_ASCII)
            println("DEBUG parseBoxes: Box encontrado - tipo: '$boxType', tamanho: $boxSize, offset: $offset")
            
            // Chamar callback para este box
            onBoxFound(boxType, offset, boxSize)
            
            // Se é um container box, navegar recursivamente
            when (boxType) {
                "moov", "trak", "mdia", "minf", "stbl" -> {
                    // Estes são container boxes, navegar dentro deles
                    val headerSize = 8
                    parseBoxes(bytes, offset + headerSize, offset + boxSize, onBoxFound)
                }
            }
            
            offset += boxSize
        }
    }
    
    /**
     * Parse do Movie Header box (mvhd) para extrair duração
     */
    private fun parseMvhdBox(bytes: ByteArray, offset: Int, @Suppress("UNUSED_PARAMETER") boxSize: Int): Int? {
        try {
            if (offset + 32 > bytes.size) return null
            
            val version = bytes[offset + 8].toInt() and 0xFF
            val timeScaleOffset = offset + if (version == 0) 20 else 28
            val durationOffset = offset + if (version == 0) 24 else 32
            
            if (durationOffset + 8 > bytes.size) return null
            
            val timescale = readInt32BigEndian(bytes, timeScaleOffset)
            val durationValue = if (version == 0) {
                readInt32BigEndian(bytes, durationOffset).toLong()
            } else {
                readInt64BigEndian(bytes, durationOffset)
            }
            
            if (timescale > 0 && durationValue > 0) {
                val duration = (durationValue / timescale).toInt()
                println("DEBUG parseMvhdBox: Duração encontrada: $duration segundos (timescale: $timescale, durationValue: $durationValue)")
                return duration
            }
        } catch (e: Exception) {
            println("DEBUG parseMvhdBox: Erro ao parsear mvhd: ${e.message}")
        }
        return null
    }
    
    /**
     * Parse do Track Header box (tkhd) para extrair dimensões
     */
    private fun parseTkhdBox(bytes: ByteArray, offset: Int, @Suppress("UNUSED_PARAMETER") boxSize: Int): Pair<Int, Int> {
        try {
            if (offset + 84 > bytes.size) return Pair(0, 0)
            
            val version = bytes[offset + 8].toInt() and 0xFF
            val dimensionsOffset = offset + if (version == 0) 76 else 88
            
            if (dimensionsOffset + 8 > bytes.size) return Pair(0, 0)
            
            val widthFixed = readInt32BigEndian(bytes, dimensionsOffset)
            val heightFixed = readInt32BigEndian(bytes, dimensionsOffset + 4)
            
            // As dimensões estão em formato fixed-point 16.16
            val width = (widthFixed shr 16)
            val height = (heightFixed shr 16)
            
            if (width > 0 && height > 0) {
                println("DEBUG parseTkhdBox: Dimensões encontradas: ${width}x${height}")
                return Pair(width, height)
            }
        } catch (e: Exception) {
            println("DEBUG parseTkhdBox: Erro ao parsear tkhd: ${e.message}")
        }
        return Pair(0, 0)
    }
    
    private fun readInt32BigEndian(bytes: ByteArray, offset: Int): Int {
        return ((bytes[offset].toInt() and 0xFF) shl 24) or
               ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
               ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
               (bytes[offset + 3].toInt() and 0xFF)
    }
    
    private fun readInt64BigEndian(bytes: ByteArray, offset: Int): Long {
        return ((bytes[offset].toLong() and 0xFF) shl 56) or
               ((bytes[offset + 1].toLong() and 0xFF) shl 48) or
               ((bytes[offset + 2].toLong() and 0xFF) shl 40) or
               ((bytes[offset + 3].toLong() and 0xFF) shl 32) or
               ((bytes[offset + 4].toLong() and 0xFF) shl 24) or
               ((bytes[offset + 5].toLong() and 0xFF) shl 16) or
               ((bytes[offset + 6].toLong() and 0xFF) shl 8) or
               (bytes[offset + 7].toLong() and 0xFF)
    }

    /**
     * Tenta extrair a duração do vídeo usando diferentes campos de metadados
     */
    private fun extractDurationFromMetadata(metadata: Metadata): Int? {
        // Lista de campos que podem conter informações de duração
        val durationFields = listOf(
            "xmpDM:duration",
            "xmpDM:Duration", 
            "duration",
            "Duration",
            "DURATION",
            "tiff:duration",
            "meta:duration",
            "Content-Duration",
            "X-Duration"
        )
        
        for (field in durationFields) {
            val value = metadata.get(field)
            println("DEBUG extractDurationFromMetadata: Campo '$field' = '$value'")
            
            if (value != null) {
                // Tenta converter para segundos
                val durationSeconds = value.toDoubleOrNull()?.toInt()
                if (durationSeconds != null && durationSeconds > 0) {
                    println("DEBUG extractDurationFromMetadata: Duração encontrada: $durationSeconds segundos")
                    return durationSeconds
                }
                
                // Tenta converter formato mm:ss ou hh:mm:ss
                val timeRegex = Regex("""(\d{1,2}):(\d{2})(?::(\d{2}))?""")
                val match = timeRegex.find(value)
                if (match != null) {
                    val hours = if (match.groups[3] != null) match.groupValues[1].toIntOrNull() ?: 0 else 0
                    val minutes = if (match.groups[3] != null) match.groupValues[2].toIntOrNull() ?: 0 else match.groupValues[1].toIntOrNull() ?: 0
                    val seconds = if (match.groups[3] != null) match.groupValues[3].toIntOrNull() ?: 0 else match.groupValues[2].toIntOrNull() ?: 0
                    
                    val totalSeconds = hours * 3600 + minutes * 60 + seconds
                    if (totalSeconds > 0) {
                        println("DEBUG extractDurationFromMetadata: Duração convertida de '$value': $totalSeconds segundos")
                        return totalSeconds
                    }
                }
            }
        }
        
        println("DEBUG extractDurationFromMetadata: Nenhuma duração encontrada nos metadados")
        return null
    }

    /**
     * Função para atualizar metadados de vídeo de uma aula existente.
     * Seria chamada quando um vídeo é associado a uma aula.
     */
    fun updateAulaVideoMetadata(
        @Suppress("UNUSED_PARAMETER") aulaId: String, 
        @Suppress("UNUSED_PARAMETER") videoMetadata: VideoMetadata, 
        @Suppress("UNUSED_PARAMETER") professorId: String
    ) {
        // Esta função seria implementada para atualizar a aula com os metadados do vídeo
        // Poderia ser integrada diretamente no ProfessorService ou chamada separadamente
        // Por enquanto é apenas um placeholder
    }
}

@Serializable
data class VideoMetadata(
    val duracaoSegundos: Int? = null,
    val resolucao: String? = null,
    val tamanhoBytes: Long,
    val codec: String? = null,
    val fps: Int? = null,
    val aspectRatio: String? = null
)

/**
 * Metadados extraídos do parser MP4 customizado
 */
private data class Mp4Metadata(
    val duration: Int? = null,
    val width: Int? = null,
    val height: Int? = null
)
