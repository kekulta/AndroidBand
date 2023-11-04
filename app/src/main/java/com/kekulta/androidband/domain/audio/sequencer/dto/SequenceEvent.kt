package com.kekulta.androidband.domain.audio.sequencer.dto

sealed class SequenceEvent(open val time: Long) {
    data class Toggle(val sampleId: Int, override val time: Long) : SequenceEvent(time)
    data class End(override val time: Long) : SequenceEvent(time)
}