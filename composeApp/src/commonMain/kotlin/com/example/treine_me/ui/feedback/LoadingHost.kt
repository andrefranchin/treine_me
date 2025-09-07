package com.example.treine_me.ui.feedback

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.CircularProgressIndicator

interface LoadingController {
    fun show()
    fun hide()
}

val LocalLoadingController = staticCompositionLocalOf<LoadingController> {
    object : LoadingController {
        override fun show() {}
        override fun hide() {}
    }
}

@Composable
fun LoadingHost(content: @Composable () -> Unit): LoadingController {
    var visible by remember { mutableStateOf(false) }
    val controller = remember {
        object : LoadingController {
            override fun show() { visible = true }
            override fun hide() { visible = false }
        }
    }
    CompositionLocalProvider(LocalLoadingController provides controller) {
        Box(Modifier.fillMaxSize()) {
            content()
            if (visible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x7FFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    return controller
}


