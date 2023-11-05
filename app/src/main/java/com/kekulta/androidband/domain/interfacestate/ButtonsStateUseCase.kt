package com.kekulta.androidband.domain.interfacestate

import com.kekulta.androidband.combineStates
import com.kekulta.androidband.domain.audio.capture.CaptureRepository
import com.kekulta.androidband.domain.audio.sequencer.SequencePlayer
import com.kekulta.androidband.domain.audio.sequencer.SequenceRecorder
import com.kekulta.androidband.domain.audio.sounds.QuickSoundsManager
import com.kekulta.androidband.presentation.framework.MicRecordingRepository
import kotlinx.coroutines.flow.StateFlow

class ButtonsStateUseCase(
    private val recorder: SequenceRecorder,
    private val player: SequencePlayer,
    private val quickSoundsManager: QuickSoundsManager,
    private val captureRepository: CaptureRepository,
    private val micRecordingRepository: MicRecordingRepository,
) {
    fun observeState(): StateFlow<InterfaceState> {
        return combineStates(
            recorder.observeRecording(),
            player.observeRecordPlaying(),
            captureRepository.observeCapturing(),
            micRecordingRepository.observeMicRecording(),
            quickSoundsManager.sounds,
        ) { isRecording, isPlaying, isCapturing, isMicRecording, quickSounds ->

            val sampleOneButton = ButtonState(
                isActive = null,
                isEnabled = all(
                    !isRecording, !isPlaying, quickSounds[0] != -1
                )
            )

            val sampleTwoButton = ButtonState(
                isActive = null,
                isEnabled = all(
                    !isRecording, !isPlaying, quickSounds[1] != -1
                )
            )

            val sampleThreeButton = ButtonState(
                isActive = null,
                isEnabled = all(
                    !isRecording, !isPlaying, quickSounds[2] != -1
                )
            )

            val sampleFourButton = ButtonState(
                isActive = null,
                isEnabled = all(
                    !isRecording, !isPlaying, quickSounds[3] != -1
                )
            )

            val libButton = ButtonState(
                isActive = null,
                isEnabled = all(
                    !isRecording, !isPlaying
                )
            )

            val micRecordingButton = ButtonState(
                isActive = isMicRecording,
                isEnabled = true
            )

            val captureButton = ButtonState(
                isActive = isCapturing,
                isEnabled = true
            )

            val recordButton = ButtonState(
                isActive = isRecording,
                isEnabled = all(
                    !isPlaying, !isCapturing, !isMicRecording
                )
            )

            val recordPlayButton = ButtonState(
                isActive = isPlaying,
                isEnabled = all(
                    !isRecording, recorder.recordExists()
                )
            )

            //TODO renderRepository
            val renderRecordButton = ButtonState(
                isActive = null,
                isEnabled = all(
                    !isRecording, recorder.recordExists()
                )
            )



            InterfaceState(
                sampleOneButtonState = sampleOneButton,
                sampleTwoButtonState = sampleTwoButton,
                sampleThreeButtonState = sampleThreeButton,
                sampleFourButtonState = sampleFourButton,
                libButtonState = libButton,
                micRecordingButtonState = micRecordingButton,
                captureButtonState = captureButton,
                recordButtonState = recordButton,
                recordPlayButtonState = recordPlayButton,
                renderButtonState = renderRecordButton,
            )
        }
    }

    private fun all(vararg boolean: Boolean): Boolean {
        return boolean.all { it }
    }
}