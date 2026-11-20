package com.example.data.ai

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

class AudioRecorderHelper(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): Boolean {
        return try {
            stopAndReleaseOnly()
            val file = File(context.cacheDir, "mymari_voice_${System.currentTimeMillis()}.m4a")
            outputFile = file

            val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mr.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(16000)
                setAudioEncodingBitRate(64000)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            recorder = mr
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start audio recording", e)
            stopAndReleaseOnly()
            false
        }
    }

    fun stopAndReadBytes(): ByteArray? {
        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            val file = outputFile
            if (file != null && file.exists()) {
                val bytes = file.readBytes()
                file.delete()
                bytes
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop audio recording", e)
            stopAndReleaseOnly()
            null
        }
    }

    fun stopAndReleaseOnly() {
        try {
            recorder?.release()
        } catch (_: Exception) {
        }
        recorder = null
    }

    private companion object {
        const val TAG = "AudioRecorderHelper"
    }
}
