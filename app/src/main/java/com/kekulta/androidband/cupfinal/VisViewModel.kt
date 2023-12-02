package com.kekulta.androidband.cupfinal

import android.graphics.Color
import com.kekulta.androidband.cupfinal.VisViewType.ELLIPSE_ONE
import com.kekulta.androidband.cupfinal.VisViewType.LINES_ONE
import com.kekulta.androidband.cupfinal.VisViewType.PRISM_ONE
import com.kekulta.androidband.cupfinal.VisViewType.SPIRAL_ONE
import com.kekulta.androidband.cupfinal.VisViewType.STAR_ONE
import com.kekulta.androidband.cupfinal.VisViewType.STAR_THREE
import com.kekulta.androidband.cupfinal.VisViewType.STAR_TWO
import com.kekulta.androidband.cupfinal.VisViewType.entries
import com.kekulta.androidband.domain.audio.samples.SampleManager
import com.kekulta.androidband.domain.audio.visualizer.VisualizerRepository
import com.kekulta.androidband.domain.viewmodels.CoroutineViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class VisViewModel(
    private val visualizerRepository: VisualizerRepository,
    private val sampleManager: SampleManager,
) : CoroutineViewModel() {

    val state = MutableStateFlow(mapOf<VisViewType, AnimationProperties>())
    val samples = MutableStateFlow("")
    val buttonState = MutableStateFlow(false)
    var collect = 0

    init {
        launchScope("waves") {
            visualizerRepository.wavesArr.collect { list ->
                if (collect == 0) {
                    if (list.isEmpty()) return@collect
                    state.value = state.value + entries.map { vis ->
                        vis to getTransformer(vis)(list)
                    }
                }
                collect++
                collect %= SKIP_FRAMES
            }
        }

        launchScope("names") {
            sampleManager.observePlayersState().collect { list ->
                samples.value = list.filter { sampleVo -> sampleVo.isPlaying }
                    .joinToString(separator = ", ") { sampleVo -> sampleVo.name }.also {
                        buttonState.value = it.isNotBlank()
                    }
            }
        }
    }

    private fun getTransformer(vis: VisViewType): Transformer {
        return when (vis) {
            ELLIPSE_ONE -> { list ->
                AnimationProperties(
                    translationY = list.getSegment(48).toFloat() * -100f
                )
            }

            STAR_ONE -> { list ->
                AnimationProperties(
                    translationX = list.getSegment(0).toFloat() * 50,
                    translationY = list.getSegment(0).toFloat() * -50,
                    scaleX = list.getSegment(0).toFloat() / 2 + 1,
                    scaleY = list.getSegment(0).toFloat() / 2 + 1,
                )
            }

            STAR_TWO -> { list ->
                AnimationProperties(
                    translationX = list.getSegment(0).toFloat() * 50,
                    translationY = list.getSegment(0).toFloat() * -50,
                    scaleX = list.getSegment(0).toFloat() / 2 + 1,
                    scaleY = list.getSegment(0).toFloat() / 2 + 1,
                )
            }

            STAR_THREE -> { list ->
                AnimationProperties(
                    translationX = list.getSegment(0).toFloat() * 50,
                    translationY = list.getSegment(0).toFloat() * -50,
                    scaleX = list.getSegment(0).toFloat() / 2 + 1,
                    scaleY = list.getSegment(0).toFloat() / 2 + 1,
                )
            }

            LINES_ONE -> { list ->
                AnimationProperties(
                    rotation = -40f, color = if (list.getSegment(32) > 0.5) {
                        Color.parseColor("#A8DB10")
                    } else {
                        Color.parseColor("#5A50E2")
                    }
                )
            }

            SPIRAL_ONE -> { list ->
                AnimationProperties(rotation = list.getSegment(16).toFloat() * 360)
            }

            PRISM_ONE -> { list ->
                AnimationProperties(
                    scaleX = list.getSegment(0).toFloat() + 1,
                    scaleY = list.getSegment(0).toFloat() + 1,
                )
            }
        }
    }

    private fun noopTransformer(list: List<Int>): AnimationProperties = AnimationProperties()

    private fun List<Int>.getSegment(fromIndex: Int, toIndex: Int = fromIndex + 15) =
        ((subList(fromIndex, toIndex).sum() / (toIndex - fromIndex + 1))) / 256.0

    fun onPauseClicked() {
        sampleManager.pauseAll()
    }

    companion object {
        const val LOG_TAG = "VisViewModel"
    }
}

typealias Transformer = (List<Int>) -> AnimationProperties

const val SKIP_FRAMES = 3
