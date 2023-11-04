package com.kekulta.androidband.presentation.framework

import android.media.audiofx.Visualizer
import com.kekulta.androidband.domain.audio.visualizer.AudioVisualizer

class AndroidVisualizer(private val audioSessionId: AudioSessionId) : AudioVisualizer {
    private var visualizer: Visualizer? = null

    override fun init() {
        visualizer = Visualizer(audioSessionId.sessionId).apply {
            captureSize = Visualizer.getCaptureSizeRange()
                .firstOrNull { it >= 128 } ?: Visualizer.getCaptureSizeRange().max()
            enabled = true
        }
    }

    override fun getWaveForm(): Int {
        val arr = ByteArray(128)

        visualizer?.getWaveForm(arr)
        return arr.toList().sumOf { it.toInt() } / 128 + 128
    }
}