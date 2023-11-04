package com.kekulta.androidband.domain.audio.sounds

import android.util.Log
import com.kekulta.androidband.data.SoundsDataStore
import com.kekulta.androidband.domain.viewmodels.AbstractCoroutineManager
import com.kekulta.androidband.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuickSoundsManager(
    private val soundsDataStore: SoundsDataStore,
) : AbstractCoroutineManager() {
    private val _state = MutableStateFlow(listOf(0, 4, -1))

    val sounds: StateFlow<List<Int>> get() = _state

    init {
        launchScope(this) {
            soundsDataStore.sounds.collect { sounds ->
                val soundsSet = sounds.map { sound -> sound.soundId }.toSet()

                _state.value =
                    _state.value.map { selectedSoundId -> if (selectedSoundId in soundsSet) selectedSoundId else -1 }
            }
        }
    }

    fun setQuickSound(soundId: Int) {
        val typeNum = when (soundsDataStore.getById(soundId).type) {
            SoundType.MELODY -> 0
            SoundType.DRUMS -> 1
            SoundType.RECORD -> 2
        }
        _state.value = _state.value.update(typeNum, soundId)

        Log.d(LOG_TAG, "QuickSounds: ${_state.value}")
    }

    fun getSoundId(num: Int): Int {
        if (num !in 0..2) {
            throw IllegalArgumentException()
        } else {
            return _state.value[num]
        }
    }

    companion object {
        const val LOG_TAG = "QuickSoundsRepository"
    }
}