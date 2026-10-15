package com.aurel.notes.core.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorderManager(private val context: Context) {

    private var recorder: MediaRecorder? = null
    var currentOutputFile: File? = null
        private set
    var isRecording: Boolean = false
        private set
    var isPaused: Boolean = false
        private set

    fun startRecording(): File? {
        val attachmentsDir = File(context.filesDir, "attachments").apply { mkdirs() }
        val outputFile = File(attachmentsDir, "voice_${System.currentTimeMillis()}.m4a")
        currentOutputFile = outputFile

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(outputFile.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
                isPaused = false
            } catch (e: IOException) {
                release()
                recorder = null
                isRecording = false
                return null
            }
        }
        return outputFile
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isRecording && !isPaused) {
            try {
                recorder?.pause()
                isPaused = true
            } catch (_: Exception) {}
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isRecording && isPaused) {
            try {
                recorder?.resume()
                isPaused = false
            } catch (_: Exception) {}
        }
    }

    fun stopRecording(): File? {
        if (!isRecording) return null
        return try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            isRecording = false
            isPaused = false
            currentOutputFile
        } catch (e: Exception) {
            recorder?.release()
            recorder = null
            isRecording = false
            isPaused = false
            null
        }
    }

    fun getAmplitude(): Int {
        return try {
            if (isRecording && !isPaused) recorder?.maxAmplitude ?: 0 else 0
        } catch (_: Exception) {
            0
        }
    }
}

class AudioPlayerManager {

    private var mediaPlayer: MediaPlayer? = null
    var isPlaying: Boolean = false
        private set

    fun play(filePath: String, onCompletion: () -> Unit) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                prepare()
                setOnCompletionListener {
                    this@AudioPlayerManager.isPlaying = false
                    onCompletion()
                }
                start()
                this@AudioPlayerManager.isPlaying = true
            } catch (e: Exception) {
                this@AudioPlayerManager.isPlaying = false
            }
        }
    }

    fun pause() {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
        }
    }

    fun resume() {
        if (!isPlaying && mediaPlayer != null) {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        isPlaying = false
    }

    fun getDuration(): Int = mediaPlayer?.duration ?: 0
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
}
