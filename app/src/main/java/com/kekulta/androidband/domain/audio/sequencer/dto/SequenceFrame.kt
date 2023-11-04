package com.kekulta.androidband.domain.audio.sequencer.dto

data class SequenceFrame(
    val soundId: Int,
    val start: Int,
    val duration: Int,
    val delay: Int,
    val speed: Double,
    val volume: Double,
)