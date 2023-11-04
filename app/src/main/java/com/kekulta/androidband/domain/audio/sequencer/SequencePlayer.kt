package com.kekulta.androidband.domain.audio.sequencer

import com.kekulta.androidband.domain.audio.samples.SampleManager
import com.kekulta.androidband.domain.audio.sequencer.dto.SequenceEvent
import com.kekulta.androidband.domain.viewmodels.AbstractCoroutineManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SequencePlayer(
    private val recorder: SequenceRecorder,
    private val sampleManager: SampleManager,
) : AbstractCoroutineManager() {
    private val _isRecordPlaying = MutableStateFlow(false)
    val isRecordPlaying: Boolean get() = _isRecordPlaying.value

    fun observeRecordPlaying(): StateFlow<Boolean> = _isRecordPlaying

    fun play() {
        launchScope(PLAY_SCOPE) {
            sampleManager.pauseAll()
            _isRecordPlaying.value = true
            val record = recorder.getRecord()

            record.initialState.forEach { sample ->
                with(sampleManager) {
                    with(sample) {
                        seekTo(sampleId, currentPosition.toLong())
                        setSpeed(sampleId, speed)
                        setVolume(sampleId, volume)
                    }
                }
            }
            record.initialState.forEach { sample ->
                if (sample.isPlaying) {
                    sampleManager.startSample(sample.sampleId)
                }
            }

            try {
                for (e in record.events) {
                    delay(e.time)
                    when (e) {
                        is SequenceEvent.Toggle -> sampleManager.toggleSample(e.sampleId)
                        is SequenceEvent.End -> sampleManager.pauseAll()
                    }
                }
            } catch (e: CancellationException) {
                sampleManager.pauseAll()
                _isRecordPlaying.value = false
                throw e
            }

            sampleManager.pauseAll()
            _isRecordPlaying.value = false
        }
    }

    fun stop() {
        sampleManager.pauseAll()
        cancelScope(PLAY_SCOPE)
        _isRecordPlaying.value = false
    }

    companion object {
        private const val PLAY_SCOPE = "play_scope"
    }
}