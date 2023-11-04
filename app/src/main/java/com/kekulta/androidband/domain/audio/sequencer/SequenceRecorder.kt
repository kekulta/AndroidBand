package com.kekulta.androidband.domain.audio.sequencer

import com.kekulta.androidband.domain.audio.sequencer.dto.SequenceEvent
import com.kekulta.androidband.domain.audio.sequencer.dto.SequenceRecord
import com.kekulta.androidband.presentation.ui.vo.SampleVo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SequenceRecorder {
    private var startMark: Long = 0
    private var endMark: Long = 0
    private var initial: List<SampleVo> = emptyList()
    private var events: MutableList<SequenceEvent> = mutableListOf()
    private val _isRecording = MutableStateFlow(false)
    val isRecording: Boolean get() = _isRecording.value

    fun observeRecording(): StateFlow<Boolean> = _isRecording

    fun start(tracks: List<SampleVo>) {
        _isRecording.value = true
        events.clear()
        startMark = System.currentTimeMillis()
        initial = tracks
    }

    fun stop() {
        _isRecording.value = false
        endMark = System.currentTimeMillis() - startMark
        events.add(SequenceEvent.End(System.currentTimeMillis() - startMark))
    }

    fun trackClick(sampleId: Int) {
        if (isRecording) {
            events.add(SequenceEvent.Toggle(sampleId, System.currentTimeMillis() - startMark))
        }
    }

    fun getRecord(): SequenceRecord {
        return SequenceRecord(
            initialState = initial,
            events = events.mapIndexed { index, audioEvent ->
                if (index == 0) {
                    audioEvent
                } else {
                    when (audioEvent) {
                        is SequenceEvent.Toggle -> SequenceEvent.Toggle(
                            audioEvent.sampleId,
                            audioEvent.time - events[index - 1].time
                        )

                        is SequenceEvent.End -> SequenceEvent.End(audioEvent.time - events[index - 1].time)
                    }

                }
            },
        )
    }

    fun recordExists() = events.isNotEmpty()

    override fun toString(): String {
        return """
            Initial state: $initial
            Events: $events
        """.trimIndent()
    }
}