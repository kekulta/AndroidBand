package com.kekulta.androidband.presentation.framework

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import com.kekulta.androidband.getTimeStamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class MicRecordingRepository(
    private val context: Context,
    private val filesManager: FilesManager,
) {
    private val _isMicRecording = MutableStateFlow(false)
    private var recorder: MediaRecorder? = null
    private var rawFile: File? = null

    fun observeMicRecording(): StateFlow<Boolean> = _isMicRecording

    fun startMicRecording() {
        _isMicRecording.value = true
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            rawFile =
                File("${filesManager.assetDir}/MicRecorded-${getTimeStamp()}").also { file ->
                    file.delete()
                    file.createNewFile()
                    setOutputFile(file)
                }
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(96000)

            prepare()
            start()
        }
    }

    fun endMicRecording() {
        _isMicRecording.value = false
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null

        rawFile?.let { file ->
            val wavFile = File("${filesManager.audiosDir}/${file.nameWithoutExtension}.wav")
            wavFile.delete()

            val exe = "-i $rawFile $wavFile"
            FFmpeg.execute(exe)

            Log.d(LOG_TAG, "Mic recorded to $wavFile")
        }
    }

    companion object {
        const val LOG_TAG = "MicRecordingRepository"
    }
}