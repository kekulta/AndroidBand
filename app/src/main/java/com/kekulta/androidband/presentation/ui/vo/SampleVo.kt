package com.kekulta.androidband.presentation.ui.vo

data class SampleVo(
    val name: String,
    val sampleId: Int,
    val soundId: Int,
    val volume: Float,
    val isSoundOn: Boolean,
    val volumeActive: Boolean,
    val speed: Float,
    val speedActive: Boolean,
    val isPlaying: Boolean,
    val duration: Int,
    val currentPosition: Int,
)