package com.example.treine_me.ui.controls

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

data class VideoData(
    val bytes: ByteArray,
    val fileName: String,
    val contentType: String,
    val size: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VideoData) return false

        if (!bytes.contentEquals(other.bytes)) return false
        if (fileName != other.fileName) return false
        if (contentType != other.contentType) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }
}

@Composable
fun VideoPicker(
    onVideoSelected: (VideoData) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Button(
        enabled = enabled,
        onClick = {
            scope.launch {
                val file = FileKit.openFilePicker()
                if (file != null) {
                    val bytes = file.readBytes()
                    val name = "video.mp4" // Nome padrão já que não temos acesso ao nome real
                    val type = "video/mp4" // Tipo padrão
                    onVideoSelected(
                        VideoData(
                            bytes = bytes,
                            fileName = name,
                            contentType = type,
                            size = bytes.size.toLong()
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) {
        Text("Selecionar vídeo")
    }
}
