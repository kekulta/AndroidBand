package com.kekulta.androidband.domain.audio.sounds

import com.kekulta.androidband.R
import com.kekulta.androidband.combineStates
import com.kekulta.androidband.data.SoundsDataStore
import com.kekulta.androidband.mapState
import com.kekulta.androidband.presentation.ui.recycler.sounds.SoundsMenuCategory
import com.kekulta.androidband.presentation.ui.vo.SoundVo
import kotlinx.coroutines.flow.StateFlow

class GetSoundsListUseCase(
    private val soundsDataStore: SoundsDataStore,
    private val quickSoundsManager: QuickSoundsManager,
) {
    fun execute(): StateFlow<List<SoundsMenuCategory>> {
        return combineStates(
            soundsDataStore.sounds,
            quickSoundsManager.sounds
        ) { sounds, quickSounds ->
            sounds.map { sound ->
                val icon = when (sound.type) {
                    SoundType.DRUMS -> R.drawable.baseline_drums
                    SoundType.MELODY -> R.drawable.baseline_music_note_24
                    SoundType.RECORD -> R.drawable.baseline_mic_24
                }

                val menuType = when (sound.type) {
                    SoundType.DRUMS, SoundType.MELODY -> R.menu.sound_asset_menu
                    SoundType.RECORD -> R.menu.sound_record_menu
                }

                SoundVo(
                    soundId = sound.soundId,
                    name = sound.name,
                    icon = icon,
                    type = sound.type,
                    checked = quickSounds.any { it == sound.soundId },
                    menu = menuType,
                )
            }
        }.mapState { sounds ->
            sounds.groupBy { soundVo -> soundVo.type }
                .map { (type, sounds) -> SoundsMenuCategory(type, sounds) }
        }
    }

    companion object {
        const val LOG_TAG = "GetSoundsListUseCase"
    }
}