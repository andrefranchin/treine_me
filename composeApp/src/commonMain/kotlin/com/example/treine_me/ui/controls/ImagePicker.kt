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


data class ImageData(
    val bytes: ByteArray,
    val fileName: String,
    val contentType: String
)

@Composable
fun ImagePicker(
    onImageSelected: (ImageData) -> Unit,
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
                    val name = "imagem.jpg"
                    val type = "image/jpeg"
                    onImageSelected(ImageData(bytes = bytes, fileName = name, contentType = type))
                }
            }
        },
        modifier = modifier
    ) {
        Text("Selecionar imagem")
    }
}
