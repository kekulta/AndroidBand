package com.kekulta.androidband.domain.audio.samples

data class Sample(
    val name: String,
    val soundId: Int,
    val player: SamplePlayer,
    val sampleId: Int,
)

