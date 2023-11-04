package com.kekulta.androidband.domain.audio.sequencer

import android.util.Log
import com.kekulta.androidband.domain.audio.sequencer.dto.SequenceEvent
import com.kekulta.androidband.domain.audio.sequencer.dto.SequenceFrame
import com.kekulta.androidband.domain.audio.sequencer.dto.SequenceRecord

class SampleFrameMapper {
    fun map(record: SequenceRecord): List<SequenceFrame> {
        val sequenceFrames = mutableListOf<SequenceFrame>()
        val lookup = record.initialState.associateBy { sampleVo -> sampleVo.sampleId }
        val points = record.initialState.associate { sampleVo ->
            Pair(sampleVo.sampleId, SamplePoint(0, sampleVo.currentPosition, sampleVo.isPlaying))
        }.toMutableMap()
        var currTime: Int = 0

        fun handleToggle(toggle: SequenceEvent.Toggle) {
            val point = points[toggle.sampleId] ?: return
            val sample = lookup[toggle.sampleId] ?: return

            if (!point.isPlaying) {
                points[toggle.sampleId] =
                    point.copy(isPlaying = true, lastInteraction = currTime)
                return
            }

            val playedTime = point.playedTime + currTime - point.lastInteraction
            points[toggle.sampleId] =
                point.copy(isPlaying = false, lastInteraction = currTime, playedTime = playedTime)

            val volume = if (sample.isSoundOn) sample.volume.toDouble() else 0.0

            sequenceFrames.add(

                SequenceFrame(
                    soundId = sample.soundId,
                    start = point.playedTime,
                    duration = currTime - point.lastInteraction,
                    delay = point.lastInteraction,
                    speed = sample.speed.toDouble(),
                    volume = volume,
                )
            )

        }

        fun handleEnd() {
            val playingAtEnd = points.filter { it.value.isPlaying }

            for ((sampleId, point) in playingAtEnd) {
                val sample = lookup[sampleId] ?: continue
                val volume = if (sample.isSoundOn) sample.volume.toDouble() else 0.0

                sequenceFrames.add(
                    SequenceFrame(
                        soundId = sample.soundId,
                        start = point.playedTime,
                        delay = point.lastInteraction,
                        duration = currTime - point.lastInteraction,
                        speed = sample.speed.toDouble(),
                        volume = volume,
                    )
                )
            }
        }

        record.events.forEach { audioEvent ->
            currTime += audioEvent.time.toInt()
            when (audioEvent) {
                is SequenceEvent.Toggle -> {
                    handleToggle(audioEvent)
                }

                is SequenceEvent.End -> {
                    handleEnd()
                }
            }
        }

        Log.d(
            LOG_TAG, """
            frames: $sequenceFrames
        """.trimIndent()
        )

        return sequenceFrames.filter { frame -> frame.volume != 0.0 }
    }

    data class SamplePoint(val lastInteraction: Int, val playedTime: Int, val isPlaying: Boolean)

    companion object {
        const val LOG_TAG = "SampleFrameMapper"
    }
}