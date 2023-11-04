package com.kekulta.androidband.presentation.framework

import android.content.Context
import com.kekulta.androidband.data.SoundsDataStore
import com.kekulta.androidband.domain.audio.samples.Sample
import com.kekulta.androidband.domain.audio.samples.SamplePlayer
import com.kekulta.androidband.domain.audio.samples.SamplesFactory
import com.kekulta.androidband.domain.audio.sounds.Sound

class SamplesFactoryImpl(
    private val context: Context,
    private val audioSessionId: AudioSessionId,
    private val soundsDataStore: SoundsDataStore,
) : SamplesFactory {
    override fun getSample(soundId: Int, name: String, sampleId: Int): Sample {
        return Sample(name, soundId, getLoopPlayer(soundId), sampleId)
    }

    private fun getLoopPlayer(soundId: Int): SamplePlayer {
        return when (val sound = soundsDataStore.getById(soundId)) {
            is Sound.Asset -> LoopSamplePlayer.create(context, sound.resId, audioSessionId)
            is Sound.Record -> LoopSamplePlayer.create(context, sound.uri, audioSessionId)
        }
    }
}