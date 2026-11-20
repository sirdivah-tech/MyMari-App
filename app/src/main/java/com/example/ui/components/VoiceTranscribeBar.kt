package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.ai.AudioRecorderHelper
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.SoftMintBorder
import com.example.ui.theme.SoftMintContainer
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun VoiceTranscribeBar(
    isTranscribing: Boolean,
    transcriptText: String?,
    errorMessage: String?,
    promptHint: String = "Tap mic & speak (e.g. \"Spent $3 on lunch via EcoCash\")",
    onAudioCaptured: (ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recorder = remember { AudioRecorderHelper(context) }

    var isRecording by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var permissionDeniedMsg by remember { mutableStateOf<String?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            permissionDeniedMsg = null
            val started = recorder.startRecording()
            if (started) {
                isRecording = true
                elapsedSeconds = 0
            } else {
                permissionDeniedMsg = "Could not access microphone right now."
            }
        } else {
            permissionDeniedMsg = "Microphone permission is needed to record voice notes."
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording && elapsedSeconds < 60) {
                delay(1000L)
                elapsedSeconds++
            }
            if (isRecording) {
                isRecording = false
                recorder.stopAndReadBytes()?.let { onAudioCaptured(it) }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder.stopAndReleaseOnly()
        }
    }

    val containerBg by animateColorAsState(
        targetValue = if (isRecording) Color(0xFFFFF7ED) else SoftMintSurface,
        label = "voice_bg"
    )
    val borderCol = if (isRecording) AlertOrange else SoftMintBorder

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(containerBg)
            .border(1.dp, borderCol, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isRecording -> AlertOrange
                                isTranscribing -> SoftMintContainer
                                else -> EmeraldBrand
                            }
                        )
                        .clickable(enabled = !isTranscribing) {
                            if (isRecording) {
                                isRecording = false
                                val bytes = recorder.stopAndReadBytes()
                                if (bytes != null) {
                                    onAudioCaptured(bytes)
                                }
                            } else {
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPerm) {
                                    permissionDeniedMsg = null
                                    if (recorder.startRecording()) {
                                        isRecording = true
                                        elapsedSeconds = 0
                                    } else {
                                        permissionDeniedMsg = "Microphone unavailable."
                                    }
                                } else {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        }
                        .testTag("btn_voice_mic"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTranscribing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = ForestGreenDark,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop recording" else "Record voice note",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = when {
                            isRecording -> "Listening... (${elapsedSeconds}s) Tap to finish"
                            isTranscribing -> "Transcribing your voice..."
                            else -> "Speak with Voice AI"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecording) AlertOrange else ForestGreenDark
                    )
                    Text(
                        text = when {
                            isRecording -> "Speak clearly into your microphone"
                            isTranscribing -> "Turning your speech into text"
                            else -> promptHint
                        },
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        if (!transcriptText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Heard: \"$transcriptText\"",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ForestGreenDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        val shownErr = permissionDeniedMsg ?: errorMessage
        if (!shownErr.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = shownErr,
                fontSize = 11.sp,
                color = Color(0xFFDC2626)
            )
        }
    }
}
