package com.kekulta.androidband.domain.audio.sequencer

import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFprobe
import com.kekulta.androidband.domain.audio.sequencer.dto.SequenceFrame
import com.kekulta.androidband.presentation.framework.AssetManager
import com.kekulta.androidband.presentation.framework.FilesManager
import java.io.File

class SequenceRenderer(
    private val filesManager: FilesManager,
    private val assetManager: AssetManager,
) {

    fun render(sequenceFrames: List<SequenceFrame>, name: String): File? {
        val groups = sequenceFrames.groupBy { it.soundId to it.speed }
        val unpackedAssets = groups.keys.associateWith { (soundId, _) ->
            unpackAsset(soundId)
        }
        val actualTimes = unpackedAssets.entries.associate { (composedKey, asset) ->
            Pair(composedKey, calculateDuration(asset))
        }
        val maxTimes = groups.entries.associate { (composedKey, samples) ->
            val maxSampleTime = samples.maxOf { (it.start + it.duration) * it.speed }
            Pair(composedKey, maxSampleTime)
        }
        val loops = actualTimes.entries.associate { (composedKey, actualTime) ->
            val maxTime = maxTimes[composedKey]!!
            val loop = maxTime.toInt() / actualTime + if (maxTime % actualTime > 0) 1 else 0
            Pair(composedKey, loop)
        }
        val loopedAssets = loops.entries.associate { (composedKey, loopsCount) ->
            val (soundId, tempo) = composedKey
            Pair(
                composedKey,
                changeSpeed(
                    loop(
                        unpackedAssets[composedKey]!!,
                        loopsCount,
                        "looped_${soundId}.wav"
                    ),
                    tempo,
                    "tempo_${(tempo * 10).toInt()}_looped_${soundId}.wav"
                )
            )
        }

        val assets = mutableListOf<File>()
        val delays = mutableListOf<Int>()

        sequenceFrames.forEachIndexed { index, sample ->
            assets.add(
                cut(
                    asset = loopedAssets[Pair(sample.soundId, sample.speed)]!!,
                    start = (sample.start * sample.speed).toInt(),
                    duration = sample.duration,
                    volume = sample.volume,
                    name = "cut_${index}_volume_${(sample.volume * 100).toInt()}_tempo_${(sample.speed * 10).toInt()}.wav"
                )
            )
            delays.add(sample.delay)
        }

        val result = try {
            merge(assets, delays, name)
        } catch (e: java.lang.IllegalArgumentException) {
            null
        }

        cleanCache()
        return result
    }

    private fun changeSpeed(asset: File, speed: Double, name: String): File {
        val outputTempo = File("${filesManager.assetDir}/$name.wav")
        outputTempo.delete()

        val exe = "-i $asset -filter:a \"atempo=$speed\" $outputTempo"
        Log.d(LOG_TAG, "tempo: $exe")
        FFmpeg.execute(exe)
        return outputTempo
    }

    private fun cut(asset: File, start: Int, duration: Int, volume: Double, name: String): File {
        val outputCut = File("${filesManager.assetDir}/$name.wav")
        outputCut.delete()
        val startSeconds = start / 1000.0
        val durationSeconds = duration / 1000.0
        val volumeTimes = volume / 100.0
        val exe =
            "-i $asset -filter:a \"volume=$volumeTimes\" -ss $startSeconds -t $durationSeconds $outputCut"

        Log.d(LOG_TAG, "cut: $exe")
        FFmpeg.execute(exe)
        return outputCut
    }

    private fun loop(asset: File, times: Int, name: String): File {
        val outputLoop = File("${filesManager.assetDir}/$name.wav")
        outputLoop.delete()
        outputLoop.delete()
        outputLoop.delete()
        val exe = "-i $asset -filter_complex \"amovie=$asset:loop=$times\" $outputLoop"

        Log.d(LOG_TAG, "loop: $exe")
        FFmpeg.execute(exe)
        return outputLoop
    }

    private fun merge(assets: List<File>, delays: List<Int>, name: String): File {
        val outputMerge = File("${filesManager.audiosDir}/$name.wav")
        outputMerge.delete()

        if (assets.size != delays.size || assets.isEmpty()) {
            throw IllegalArgumentException()
        }
        val args = assets.joinToString(separator = " ") { "-i $it" }
        val delaysFormatted = List(assets.size) { index ->
            val delay = delays[index]
            "[$index]adelay=$delay|$delay[s$index]"
        }.joinToString(";")
        val mix = List(assets.size) { index ->
            "[s$index]"
        }.joinToString("")
        val size = assets.size
        val exe =
            "$args -filter_complex \"$delaysFormatted;${mix}amix=$size\" -c:v copy -shortest $outputMerge"

        Log.d(LOG_TAG, "merge: $exe")
        FFmpeg.execute(exe)

        return outputMerge
    }

    private fun calculateDuration(asset: File): Int {
        //analog to ffprobe -i <file> -show_entries format=duration -v quiet -of csv="p=0"

        val meta = FFprobe.getMediaInformation(asset.absolutePath)
        return (meta.duration.toDouble() * 1000).toInt()
    }

    private fun cleanCache() {
        filesManager.assetDir.deleteRecursively()
    }

    private fun unpackAsset(soundId: Int): File {
        return assetManager.unpackAsset(soundId)
    }

    companion object {
        private const val LOG_TAG = "AudioRenderer"
    }
}