package com.aurel.notes.ui.voice

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.audio.AudioPlayerManager
import com.aurel.notes.core.audio.AudioRecorderManager
import com.aurel.notes.core.designsystem.AurelTheme
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun VoiceRecordDialog(
    onAttachVoice: (File, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val recorder = remember { AudioRecorderManager(context) }
    val player = remember { AudioPlayerManager() }

    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var recordingDurationSeconds by remember { mutableIntStateOf(0) }
    var recordedFile by remember { mutableStateOf<File?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var amplitude by remember { mutableIntStateOf(0) }

    // Live timer while recording
    LaunchedEffect(isRecording, isPaused) {
        while (isRecording && !isPaused) {
            delay(1000)
            recordingDurationSeconds++
            amplitude = recorder.getAmplitude()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder.stopRecording()
            player.stop()
        }
    }

    val minutes = recordingDurationSeconds / 60
    val seconds = recordingDurationSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Voice Capture", style = AurelTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Waveform / pulsing ring
                val scale by animateFloatAsState(
                    targetValue = if (isRecording && !isPaused) 1.15f else 1.0f,
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .size((76 * scale).dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) {
                                if (isPaused) Color(0xFFE08D3C).copy(alpha = 0.2f) else AurelTheme.colors.primaryAccent.copy(alpha = 0.2f)
                            } else {
                                AurelTheme.colors.divider
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRecording) {
                                    if (isPaused) Color(0xFFE08D3C) else AurelTheme.colors.primaryAccent
                                } else {
                                    Color(0xFF777481)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = formattedTime,
                    style = AurelTheme.typography.headlineLarge,
                    color = AurelTheme.colors.primaryInk,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when {
                        isRecording && !isPaused -> "Recording voice note..."
                        isRecording && isPaused -> "Paused"
                        recordedFile != null -> "Recording ready to attach"
                        else -> "Tap Start below to record"
                    },
                    style = AurelTheme.typography.bodySmall,
                    color = AurelTheme.colors.secondaryMuted
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isRecording && recordedFile == null) {
                        Button(
                            onClick = {
                                val file = recorder.startRecording()
                                if (file != null) {
                                    isRecording = true
                                    recordingDurationSeconds = 0
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AurelTheme.colors.primaryAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Start Recording")
                        }
                    } else if (isRecording) {
                        // Pause / Resume
                        IconButton(onClick = {
                            if (isPaused) {
                                recorder.resumeRecording()
                                isPaused = false
                            } else {
                                recorder.pauseRecording()
                                isPaused = true
                            }
                        }) {
                            Icon(
                                if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                contentDescription = "Pause/Resume",
                                tint = AurelTheme.colors.primaryInk
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Stop
                        IconButton(
                            onClick = {
                                val file = recorder.stopRecording()
                                isRecording = false
                                recordedFile = file
                            }
                        ) {
                            Icon(Icons.Filled.Stop, contentDescription = "Stop", tint = Color(0xFFD94848), modifier = Modifier.size(32.dp))
                        }
                    } else if (recordedFile != null) {
                        // Play / Pause playback
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    player.pause()
                                    isPlaying = false
                                } else {
                                    player.play(recordedFile!!.absolutePath) {
                                        isPlaying = false
                                    }
                                    isPlaying = true
                                }
                            }
                        ) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Playback",
                                tint = AurelTheme.colors.primaryAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (recordedFile != null) {
                Button(
                    onClick = {
                        val duration = (recordingDurationSeconds * 1000).toLong()
                        onAttachVoice(recordedFile!!, duration)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AurelTheme.colors.primaryAccent)
                ) {
                    Text("Attach to Note")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
