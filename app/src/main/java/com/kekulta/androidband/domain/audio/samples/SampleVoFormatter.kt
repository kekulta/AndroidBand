package com.kekulta.androidband.domain.audio.samples

import com.kekulta.androidband.domain.audio.sequencer.SequenceRecorder
import com.kekulta.androidband.presentation.ui.vo.SampleVo

class SampleVoFormatter(
    private val recorder: SequenceRecorder,
) {
    fun format(list: List<Sample>): List<SampleVo> {
        return list.map { sample ->
            with(sample) {
                SampleVo(
                    name = name,
                    sampleId = sampleId,
                    soundId = soundId,
                    isPlaying = player.isPlaying,
                    isSoundOn = player.isSoundOn,
                    volume = player.volume,
                    speed = player.speed,
                    duration = player.duration,
                    currentPosition = player.currentPosition,
                    volumeActive = !recorder.isRecording,
                    speedActive = !recorder.isRecording,
                )
            }
        }
    }
}