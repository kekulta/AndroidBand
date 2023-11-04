package com.kekulta.androidband.domain.audio.samples

import kotlin.random.Random

data class Sample(
    val name: String,
    val soundId: Int,
    val player: SamplePlayer,
    val sampleId: Int = Random.nextInt(),
)

