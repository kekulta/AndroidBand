package com.kekulta.androidband.domain.viewmodels

import android.Manifest
import androidx.lifecycle.viewModelScope
import com.kekulta.androidband.R
import com.kekulta.androidband.data.PersistenceManager
import com.kekulta.androidband.data.SoundsDataStore
import com.kekulta.androidband.domain.audio.capture.CaptureRepository
import com.kekulta.androidband.domain.audio.samples.SampleManager
import com.kekulta.androidband.domain.audio.sequencer.SampleFrameMapper
import com.kekulta.androidband.domain.audio.sequencer.SequencePlayer
import com.kekulta.androidband.domain.audio.sequencer.SequenceRecorder
import com.kekulta.androidband.domain.audio.sequencer.SequenceRenderer
import com.kekulta.androidband.domain.audio.sounds.QuickSoundsManager
import com.kekulta.androidband.domain.audio.visualizer.VisualizerRepository
import com.kekulta.androidband.domain.interfacestate.ButtonsStateUseCase
import com.kekulta.androidband.domain.interfacestate.InterfaceState
import com.kekulta.androidband.getTimeStamp
import com.kekulta.androidband.presentation.framework.MicRecordingRepository
import com.kekulta.androidband.presentation.framework.PermissionManager
import com.kekulta.androidband.presentation.framework.PermissionRequest
import com.kekulta.androidband.presentation.framework.RationaleCallback
import com.kekulta.androidband.presentation.framework.ResourceManager
import com.kekulta.androidband.presentation.ui.events.ControlType
import com.kekulta.androidband.presentation.ui.events.MainFragmentEvent
import com.kekulta.androidband.presentation.ui.events.SampleUiEvent
import com.kekulta.androidband.presentation.ui.vo.SampleVo
import com.kekulta.androidband.presentation.ui.vo.WaveUnitVo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

class MainFragmentViewModel(
    private val sampleManager: SampleManager,
    private val quickSoundsManager: QuickSoundsManager,
    private val visualizerRepository: VisualizerRepository,
    private val recorder: SequenceRecorder,
    private val player: SequencePlayer,
    private val buttonsStateUseCase: ButtonsStateUseCase,
    private val sequenceRenderer: SequenceRenderer,
    private val sampleFrameMapper: SampleFrameMapper,
    private val soundsDataStore: SoundsDataStore,
    private val captureRepository: CaptureRepository,
    private val permissionManager: PermissionManager,
    private val micRecordingRepository: MicRecordingRepository,
    private val resourceManager: ResourceManager,
    private val persistenceManager: PersistenceManager,
) : CoroutineViewModel() {
    val state: StateFlow<List<SampleVo>> get() = sampleManager.observePlayersState()
    val wavesState: StateFlow<List<WaveUnitVo>> get() = visualizerRepository.wavesState
    val interfaceState: StateFlow<InterfaceState>
        get() = buttonsStateUseCase.observeState()

    private val _controlChannel = Channel<ControlType>()
    val controlChannel: Flow<ControlType> get() = _controlChannel.receiveAsFlow()
    private val inputCallbacksHolder = mutableMapOf<Int, (String?) -> Unit>()
    private val _eventChannel = Channel<MainFragmentEvent>()
    val eventChannel: Flow<MainFragmentEvent> = _eventChannel.receiveAsFlow()

    private var isJustLaunched = true

    fun renderRecord(): File {
        val frames = sampleFrameMapper.map(recorder.getRecord())
        return sequenceRenderer.render(sequenceFrames = frames, name = "Rendered-${getTimeStamp()}")
    }

    fun startWaves(rationaleCallback: RationaleCallback) {
        if (isJustLaunched) {
            isJustLaunched = false
            withAudioPermissions(
                rationaleCallback = rationaleCallback,
                forceRationale = true
            ) { granted ->
                if (granted) {
                    visualizerRepository.init()
                }
            }
        }

        launchScope(WAVE_SCOPE) {
            while (isActive) {
                visualizerRepository.emitWave()
                delay(200)
            }
        }
    }

    fun restoreSamples() {
        if (isJustLaunched) {
            persistenceManager.load()
        }
    }

    fun saveSamples() {
        persistenceManager.save()
    }

    fun stopWaves() {
        cancelScope(WAVE_SCOPE)
    }

    fun onNavigateToLib() {
        soundsDataStore.updateSounds()
    }

    fun onQuickSampleClicked(num: Int) {
        addSample(
            quickSoundsManager.getSoundId(num)
        )
    }

    fun onRecordingClick() {
        if (recorder.isRecording) {
            recorder.stop()
        } else {
            recorder.start(state.value)
        }
    }

    fun onRecordingPlayClick() {
        if (player.isRecordPlaying) {
            player.stop()
        } else {
            player.play()
        }
    }


    fun onSampleUiEvent(sampleUiEvent: SampleUiEvent) {
        when (sampleUiEvent) {
            is SampleUiEvent.PlayClicked -> onPlayClicked(sampleUiEvent.sampleId)
            is SampleUiEvent.ResetClicked -> onReset(sampleUiEvent.sampleId)
            is SampleUiEvent.SoundClicked -> onToggleSound(sampleUiEvent.sampleId)
            is SampleUiEvent.SpeedChanged -> onSpeedChanged(
                sampleUiEvent.sampleId,
                sampleUiEvent.speed,
            )

            is SampleUiEvent.VolumeChanged -> onVolumeChanged(
                sampleUiEvent.sampleId,
                sampleUiEvent.volume,
            )

            is SampleUiEvent.Delete -> onDelete(sampleUiEvent.sampleId)
            is SampleUiEvent.Rename -> onRename(sampleUiEvent.sampleId)
        }
    }

    fun onEndCapturing() {
        captureRepository.endCapturing()
    }

    fun isCapturing(): Boolean {
        return captureRepository.observeCapturing().value
    }

    fun startMicRecording(rationaleCallback: RationaleCallback) {
        withAudioPermissions(
            rationaleCallback
        ) { granted ->
            if (granted) {
                micRecordingRepository.startMicRecording()
            }
        }
    }

    fun endMicRecording() {
        micRecordingRepository.endMicRecording()
    }

    fun isMicRecording(): Boolean {
        return micRecordingRepository.observeMicRecording().value
    }

    fun goToControl(target: ControlType) {
        viewModelScope.launch {
            _controlChannel.send(target)
        }
    }

    fun withAudioPermissions(
        rationaleCallback: RationaleCallback? = null,
        forceRationale: Boolean = false,
        resultCallback: (granted: Boolean) -> Unit
    ) {
        permissionManager.requestPermission(
            PermissionRequest(
                permission = Manifest.permission.RECORD_AUDIO,
                requestCode = AUDIO_PERMISSION_REQUEST_CODE,
                forceRationale = forceRationale,
                rationaleCallback = rationaleCallback,
                resultCallback = resultCallback,
            )
        )
    }

    fun onInputResult(inputId: Int, inputResult: String?) {
        inputCallbacksHolder[inputId]?.invoke(inputResult)
        inputCallbacksHolder.remove(inputId)
    }

    private fun onRename(sampleId: Int) {
        val inputId = Random.nextInt()
        inputCallbacksHolder[inputId] = { newName ->
            if (newName != null) {
                sampleManager.renameSample(sampleId, newName)
            }
        }

        viewModelScope.launch {
            _eventChannel.send(
                MainFragmentEvent.Input(
                    inputId = inputId,
                    title = resourceManager.getString(R.string.rename_to),
                    message = ""
                )
            )
        }
    }

    private fun onDelete(sampleId: Int) {
        sampleManager.deleteSample(sampleId)
    }

    private fun onReset(sampleId: Int) {
        sampleManager.seekTo(sampleId, 0)
    }

    private fun onToggleSound(sampleId: Int) {
        sampleManager.toggleSound(sampleId)
    }

    private fun onPlayClicked(sampleId: Int) {
        sampleManager.toggleSample(sampleId)
    }

    private fun onVolumeChanged(sampleId: Int, volume: Float) {
        sampleManager.setVolume(sampleId, volume)
    }

    private fun onSpeedChanged(sampleId: Int, speed: Float) {
        sampleManager.setSpeed(sampleId, speed)
    }

    private fun addSample(sampleId: Int) {
        sampleManager.addSample(sampleId)
    }

    override fun onCleared() {
        super.onCleared()
        permissionManager.clear()
        sampleManager.clear()
        quickSoundsManager.clear()
        player.clear()
        soundsDataStore.clear()
        persistenceManager.clear()
    }

    companion object {
        private const val WAVE_SCOPE = "wave_scope"
        private const val AUDIO_PERMISSION_REQUEST_CODE = 7777
        private const val LOG_TAG = "MainFragmentViewModel"
    }
}