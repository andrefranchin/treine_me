package com.example.treine_me.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Tentando importar com chaintech apenas
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.PlayerSpeed
import chaintech.videoplayer.model.ScreenResize
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.preview.VideoPreviewComposable

/**
 * Componente de player de vídeo para reprodução inline usando Compose Multiplatform Media Player
 */
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    showControls: Boolean = true,
    isLooping: Boolean = false,
    startTimeInSeconds: Float = 0f
) {s
    val playerHost = remember(videoUrl) {
        MediaPlayerHost(
            mediaUrl = videoUrl,
            isPaused = !autoPlay,
            isMuted = false,
            initialSpeed = PlayerSpeed.X1,
            initialVideoFitMode = ScreenResize.FIT,
            isLooping = isLooping,
            startTimeInSeconds = startTimeInSeconds,
            isFullScreen = false
        )
    }

    VideoPlayerComposable(
        modifier = modifier,
        playerHost = playerHost,
        playerConfig = VideoPlayerConfig(
            isPauseResumeEnabled = true,
            isSeekBarVisible = showControls,
            isDurationVisible = showControls,
            seekBarThumbColor = MaterialTheme.colorScheme.primary,
            seekBarActiveTrackColor = MaterialTheme.colorScheme.primary,
            seekBarInactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            durationTextColor = Color.White,
            seekBarBottomPadding = 10.dp,
            pauseResumeIconSize = 40.dp,
            isAutoHideControlEnabled = true,
            controlHideIntervalSeconds = 5,
            isFastForwardBackwardEnabled = true,
            isMuteControlEnabled = true,
            isSpeedControlEnabled = true,
            isFullScreenEnabled = true,
            isScreenLockEnabled = false
        )
    )
}

/**
 * Dialog de player de vídeo em tela cheia
 */
@Composable
fun VideoPlayerDialog(
    videoUrl: String,
    title: String? = null,
    onDismiss: () -> Unit,
    autoPlay: Boolean = true,
    isLooping: Boolean = false
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Player de vídeo
            VideoPlayer(
                videoUrl = videoUrl,
                modifier = Modifier.fillMaxSize(),
                autoPlay = autoPlay,
                showControls = true,
                isLooping = isLooping
            )
            
            // Barra superior com título e botão fechar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Preview de vídeo compacto para uso em cards com thumbnails reais
 */
@Composable
fun VideoPreview(
    videoUrl: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    frameCount: Int = 5
) {
    Card(
        modifier = modifier,
        onClick = onClick ?: {}
    ) {
        Box {
            VideoPreviewComposable(
                url = videoUrl,
                frameCount = frameCount,
                    loadingIndicatorColor = MaterialTheme.colorScheme.primary
            )
            
            // Overlay com botão de play
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                FloatingActionButton(
                    onClick = onClick ?: {},
                    modifier = Modifier.size(56.dp),
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Reproduzir",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

/**
 * Componente de thumbnail de vídeo com botão de play (fallback)
 */
@Composable
fun VideoThumbnail(
    videoUrl: String,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder para thumbnail
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Vídeo",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Vídeo disponível",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Botão de play sobreposto
        FloatingActionButton(
            onClick = onPlayClick,
            modifier = Modifier.align(Alignment.Center),
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Reproduzir",
                tint = Color.White
            )
        }
    }
}