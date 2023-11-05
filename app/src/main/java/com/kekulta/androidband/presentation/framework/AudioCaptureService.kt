package com.kekulta.androidband.presentation.framework

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.arthenica.mobileffmpeg.FFmpeg
import com.kekulta.androidband.R
import com.kekulta.androidband.getTimeStamp
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread
import kotlin.experimental.and


class AudioCaptureService : Service() {

    private val filesManager: FilesManager by inject()

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var rawFile: File? = null
    private var mediaProjection: MediaProjection? = null

    private lateinit var audioCaptureThread: Thread
    private var audioRecord: AudioRecord? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(
            SERVICE_ID,
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).build()
        )

        // use applicationContext to avoid memory leak on Android 10.
        // see: https://partnerissuetracker.corp.google.com/issues/139732252
        mediaProjectionManager =
            applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Audio Capture Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return if (intent != null) {
            when (intent.action) {
                ACTION_START -> {
                    mediaProjection =
                        mediaProjectionManager.getMediaProjection(
                            Activity.RESULT_OK,
                            intent.getParcelableExtra(EXTRA_RESULT_DATA)!!
                        ) as MediaProjection
                    startAudioCapture()
                    Service.START_STICKY
                }

                ACTION_STOP -> {
                    stopAudioCapture()
                    Service.START_NOT_STICKY
                }

                else -> throw IllegalArgumentException("Unexpected action received: ${intent.action}")
            }
        } else {
            Service.START_NOT_STICKY
        }
    }

    // Permissions checked but lint can't figure it out
    @SuppressLint("MissingPermission")
    private fun startAudioCapture() {
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection!!)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        /**
         * Using hardcoded values for the audio format, Mono PCM samples with a sample rate of 8000Hz
         * These can be changed according to your application's needs
         */
        val audioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(44100)
            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
            .build()

        if (!checkPermissions()) return
        audioRecord = AudioRecord.Builder()
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(BUFFER_SIZE_IN_BYTES)
            .setAudioPlaybackCaptureConfig(config)
            .build()

        audioRecord!!.startRecording()
        audioCaptureThread = thread(start = true) {
            val outputFile = createAudioFile()
            Log.d(LOG_TAG, "Created file for capture target: ${outputFile.absolutePath}")
            writeAudioToFile(outputFile)
        }
    }

    private fun createAudioFile(): File {
        val fileName = "Capture-${getTimeStamp()}.pcm"
        return File("${filesManager.assetDir}/$fileName")
    }

    private fun writeAudioToFile(outputFile: File) {
        val fileOutputStream = FileOutputStream(outputFile)
        val capturedAudioSamples = ShortArray(NUM_SAMPLES_PER_READ)

        while (!audioCaptureThread.isInterrupted) {
            audioRecord?.read(capturedAudioSamples, 0, NUM_SAMPLES_PER_READ)
            fileOutputStream.write(
                capturedAudioSamples.toByteArray(),
                0,
                BUFFER_SIZE_IN_BYTES
            )
        }

        fileOutputStream.close()
        Log.d(
            LOG_TAG,
            "Audio capture finished for ${outputFile.absolutePath}. File size is ${outputFile.length()} bytes."
        )

        rawFile = outputFile
    }

    private fun stopAudioCapture() {
        requireNotNull(mediaProjection) { "Tried to stop audio capture, but there was no ongoing capture in place!" }

        audioCaptureThread.interrupt()
        audioCaptureThread.join()

        formatFile()

        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null

        mediaProjection!!.stop()
        stopSelf()
    }

    private fun formatFile() {
        val wavFile = File("${filesManager.audiosDir}/Captured-${getTimeStamp()}.wav")
        wavFile.delete()
        if (rawFile == null) {
            Log.e(LOG_TAG, "No record found!")
            Toast.makeText(
                applicationContext,
                getString(R.string.capturing_failure_snackbar).format(wavFile.name),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val exe = "-f s16le -ar 44100 -ac 1 -i $rawFile $wavFile"
            FFmpeg.execute(exe)
            rawFile?.deleteRecursively()
            Log.d(LOG_TAG, "File formatted to wav: $wavFile")

            Toast.makeText(
                applicationContext,
                getString(R.string.capturing_success_snackbar).format(wavFile.name),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun ShortArray.toByteArray(): ByteArray {
        // Samples get translated into bytes following little-endianness:
        // least significant byte first and the most significant byte last
        val bytes = ByteArray(size * 2)
        for (i in 0 until size) {
            bytes[i * 2] = (this[i] and 0x00FF).toByte()
            bytes[i * 2 + 1] = (this[i].toInt() shr 8).toByte()
            this[i] = 0
        }
        return bytes
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val LOG_TAG = "AudioCaptureService"
        private const val SERVICE_ID = 123
        private const val NOTIFICATION_CHANNEL_ID = "AudioCapture channel"

        private const val NUM_SAMPLES_PER_READ = 1024
        private const val BYTES_PER_SAMPLE = 2 // 2 bytes since we hardcoded the PCM 16-bit format
        private const val BUFFER_SIZE_IN_BYTES = NUM_SAMPLES_PER_READ * BYTES_PER_SAMPLE

        const val ACTION_START = "AudioCaptureService:Start"
        const val ACTION_STOP = "AudioCaptureService:Stop"
        const val EXTRA_RESULT_DATA = "AudioCaptureService:Extra:ResultData"
    }
}