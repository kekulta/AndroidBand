package com.kekulta.androidband.domain.audio.samples

import com.kekulta.androidband.combineStates
import com.kekulta.androidband.domain.audio.sequencer.SequenceRecorder
import com.kekulta.androidband.presentation.ui.vo.SampleVo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SamplesRepository(
    private val sampleVoFormatter: SampleVoFormatter,
    private val sequenceRecorder: SequenceRecorder,
) {
    private val samples: MutableMap<Int, Sample> = mutableMapOf()
    private val _state = MutableStateFlow<List<Sample>>(emptyList())

    // used to force update state
    private val _flag = MutableStateFlow(false)

    val state: StateFlow<List<SampleVo>>
        get() = combineStates(_state, sequenceRecorder.observeRecording(), _flag) { list, _, _ ->
            sampleVoFormatter.format(list)
        }

    fun allSamples(block: Sample.() -> Unit) {
        samples.forEach { (_, sample) -> sample.block() }
        updateState()
    }

    fun onSample(sampleId: Int, block: Sample.() -> Unit): Boolean {
        samples[sampleId]?.block()
        updateState()
        return samples[sampleId] != null
    }

    fun addSample(sampleId: Int, sample: Sample) {
        samples[sampleId] = sample
        updateState()
    }

    fun removeSample(sampleId: Int) {
        samples[sampleId]?.let { sample ->
            if (sample.player.isPlaying) sample.player.pause()
            sample.player.release()
            samples.remove(sampleId)
            updateState()
        }
    }

    fun renameSample(sampleId: Int, newName: String) {
        samples[sampleId]?.let { sample ->
            samples[sampleId] = sample.copy(name = newName)
            updateState()
        }
    }


    fun updateState() {
        _state.value = samples.values.toList()
        _flag.value = !_flag.value
    }

    companion object {
        const val LOG_TAG = "SamplesRepository"
    }
}