package com.kekulta.androidband.domain.viewmodels

import androidx.core.net.toFile
import androidx.lifecycle.viewModelScope
import com.kekulta.androidband.R
import com.kekulta.androidband.data.SoundsDataStore
import com.kekulta.androidband.domain.audio.samples.SampleManager
import com.kekulta.androidband.domain.audio.sounds.GetSoundsListUseCase
import com.kekulta.androidband.domain.audio.sounds.QuickSoundsManager
import com.kekulta.androidband.domain.audio.sounds.Sound
import com.kekulta.androidband.presentation.framework.ResourceManager
import com.kekulta.androidband.presentation.ui.events.LibraryFragmentEvent
import com.kekulta.androidband.presentation.ui.events.SoundUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random


class LibraryFragmentViewModel(
    getSoundsListUseCase: GetSoundsListUseCase,
    private val sampleManager: SampleManager,
    private val soundsDataStore: SoundsDataStore,
    private val quickSoundsManager: QuickSoundsManager,
    private val resourceManager: ResourceManager,
) : CoroutineViewModel() {
    val state = getSoundsListUseCase.execute()
    private val inputCallbacksHolder = mutableMapOf<Int, (String?) -> Unit>()
    private val _eventChannel = Channel<LibraryFragmentEvent>()
    val eventChannel: Flow<LibraryFragmentEvent> = _eventChannel.receiveAsFlow()

    fun onSoundUiEvent(soundUIEvent: SoundUiEvent) {
        when (soundUIEvent) {
            is SoundUiEvent.Add -> addSample(soundUIEvent.soundId)
            is SoundUiEvent.Select -> selectSound(soundUIEvent.soundId)
            is SoundUiEvent.Delete -> delete(soundUIEvent.soundId)
            is SoundUiEvent.Rename -> rename(soundUIEvent.soundId)
            is SoundUiEvent.Share -> share(soundUIEvent.soundId)
        }
    }

    private fun selectSound(soundId: Int) {
        quickSoundsManager.setQuickSound(soundId)
    }

    private fun addSample(soundId: Int) {
        sampleManager.addSample(soundId)
    }

    private fun share(soundId: Int) {
        val sound = soundsDataStore.getById(soundId)
        if (sound is Sound.Record) {
            val shareFile = sound.uri.toFile()
            viewModelScope.launch {
                _eventChannel.send(LibraryFragmentEvent.Share(shareFile))
            }
        }
    }

    fun onInputResult(inputId: Int, inputResult: String?) {
        inputCallbacksHolder[inputId]?.invoke(inputResult)
        inputCallbacksHolder.remove(inputId)
    }

    private fun rename(soundId: Int) {
        val inputId = Random.nextInt()
        inputCallbacksHolder[inputId] = { newName ->
            if (newName != null) {
                soundsDataStore.renameSound(soundId, newName)
            }
        }

        viewModelScope.launch {
            _eventChannel.send(
                LibraryFragmentEvent.Input(
                    inputId = inputId,
                    title = resourceManager.getString(R.string.rename_to),
                    message = ""
                )
            )
        }
    }

    private fun delete(soundId: Int) {
        soundsDataStore.deleteSound(soundId)
    }
}