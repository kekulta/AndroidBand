package com.kekulta.androidband.domain.audio.samples

import com.kekulta.androidband.data.SoundsDataStore
import com.kekulta.androidband.domain.audio.sequencer.SequenceRecorder
import com.kekulta.androidband.domain.audio.visualizer.VisualizerRepository
import com.kekulta.androidband.domain.viewmodels.AbstractCoroutineManager
import com.kekulta.androidband.presentation.ui.vo.SampleVo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SampleManager(
    private val soundsDataStore: SoundsDataStore,
    private val samplesRepository: SamplesRepository,
    private val recorder: SequenceRecorder,
    private val visualizerRepository: VisualizerRepository,
    private val mediaPlayerRepository: SamplesFactory,
) : AbstractCoroutineManager() {

    init {
        launchScope(this) {
            soundsDataStore.sounds.collect { sounds ->
                val soundsIds = sounds.map { sound -> sound.soundId }.toSet()
                val samplesSoundsIds =
                    samplesRepository.state.value.map { sampleVo -> sampleVo.soundId }.toSet()
                val samplesForSounds =
                    samplesRepository.state.value.groupBy { sampleVo -> sampleVo.soundId }

                val deletedSoundsIds = samplesSoundsIds - soundsIds

                deletedSoundsIds.forEach { soundsIs ->
                    samplesForSounds[soundsIs]?.forEach { sampleVo ->
                        deleteSample(sampleVo.sampleId)
                    }
                }
            }
        }
    }

    fun observePlayersState(): StateFlow<List<SampleVo>> = samplesRepository.state

    fun startSample(sampleId: Int) {
        recorder.trackClick(sampleId)
        samplesRepository.onSample(sampleId) {
            player.start()

            launchScope(sampleId) {
                launch {
                    while (isActive) {
                        samplesRepository.updateState()
                        delay(500)
                    }
                }

                launch {
                    visualizerRepository.startWave()
                    try {
                        awaitCancellation()
                    } catch (e: CancellationException) {
                        visualizerRepository.stopWave()
                    }
                }
            }
        }
    }

    fun pauseSample(sampleId: Int) {
        recorder.trackClick(sampleId)

        samplesRepository.onSample(sampleId) {
            if (player.isPlaying) {
                player.pause()
            }
            cancelScope(sampleId)
        }
    }

    fun pauseAll() {
        samplesRepository.allSamples {
            pauseSample(sampleId)
        }
    }

    fun toggleSample(sampleId: Int) {
        samplesRepository.onSample(sampleId) {
            if (player.isPlaying) {
                pauseSample(sampleId)
            } else {
                startSample(sampleId)
            }
        }
    }

    fun seekTo(sampleId: Int, millis: Long) {
        samplesRepository.onSample(sampleId) {
            player.seekTo(millis)
        }
    }

    fun addSample(soundId: Int, name: String = soundsDataStore.getById(soundId).name) {
        val sample = mediaPlayerRepository.getSample(soundId, name)
        samplesRepository.addSample(sample.sampleId, sample)
    }

    fun setVolume(sampleId: Int, volume: Float) {
        samplesRepository.onSample(sampleId) { player.setVolume(volume) }
    }

    fun toggleSound(sampleId: Int) {
        samplesRepository.onSample(sampleId) { player.toggleSound(!player.isSoundOn) }
    }

    fun setSpeed(sampleId: Int, speed: Float) {
        samplesRepository.onSample(sampleId) { player.setSpeed(speed) }
    }

    fun deleteSample(sampleId: Int) {
        pauseSample(sampleId)
        samplesRepository.removeSample(sampleId)
    }

    fun renameSample(sampleId: Int, newName: String) {
        samplesRepository.renameSample(sampleId, newName)
    }
}