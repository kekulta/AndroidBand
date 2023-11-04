package com.kekulta.androidband.domain.audio.visualizer

import android.util.Log
import com.kekulta.androidband.presentation.ui.vo.WaveUnitVo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class VisualizerRepository(
    private val visualizer: AudioVisualizer
) {
    private var wavesCollector = 0

    private val _waves = ArrayDeque<WaveUnitVo>(256)
    private val _wavesState = MutableStateFlow(_waves.toList())
    val wavesState: StateFlow<List<WaveUnitVo>> get() = _wavesState


    fun clearWave() {
        _waves.clear()
        updateState()
    }

    fun emitWave() {
        if (wavesCollector == 0) return

        _waves.addLast(
            WaveUnitVo(
                waveId = Random.nextInt(),
                form = visualizer.getWaveForm(),
            )
        )
        if (_waves.size > 256) {
            _waves.removeFirst()
        }
        updateState()
    }

    fun startWave() {
        wavesCollector++
        Log.d(LOG_TAG, "startWave: $wavesCollector")
    }

    fun stopWave() {
        wavesCollector--
        Log.d(LOG_TAG, "stopWave: $wavesCollector")
    }

    private fun updateState() {
        _wavesState.value = _waves.toList()
    }

    fun init() {
        visualizer.init()
    }

    companion object {
        const val LOG_TAG = "VisualizerRepository"
    }
}