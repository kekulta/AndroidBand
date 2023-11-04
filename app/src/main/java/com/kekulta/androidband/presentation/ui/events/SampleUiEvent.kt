package com.kekulta.androidband.presentation.ui.events

sealed class SampleUiEvent(open val sampleId: Int) {
    data class PlayClicked(override val sampleId: Int) : SampleUiEvent(sampleId)
    data class SoundClicked(override val sampleId: Int) : SampleUiEvent(sampleId)
    data class ResetClicked(override val sampleId: Int) : SampleUiEvent(sampleId)
    data class VolumeChanged(override val sampleId: Int, val volume: Float) :
        SampleUiEvent(sampleId)
    data class SpeedChanged(override val sampleId: Int, val speed: Float) : SampleUiEvent(sampleId)
    data class Rename(override val sampleId: Int) : SampleUiEvent(sampleId)
    data class Delete(override val sampleId: Int) : SampleUiEvent(sampleId)
}