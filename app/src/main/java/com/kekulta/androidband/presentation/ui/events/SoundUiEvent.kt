package com.kekulta.androidband.presentation.ui.events

sealed class SoundUiEvent(open val soundId: Int) {
    data class Add(override val soundId: Int) : SoundUiEvent(soundId)
    data class Select(override val soundId: Int) : SoundUiEvent(soundId)
    data class Share(override val soundId: Int) : SoundUiEvent(soundId)
    data class Rename(override val soundId: Int) : SoundUiEvent(soundId)
    data class Delete(override val soundId: Int) : SoundUiEvent(soundId)
}