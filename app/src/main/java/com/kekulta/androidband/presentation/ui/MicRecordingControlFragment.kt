package com.kekulta.androidband.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kekulta.androidband.R
import com.kekulta.androidband.bind
import com.kekulta.androidband.databinding.FragmentMicRecordingBinding
import com.kekulta.androidband.domain.viewmodels.MainFragmentViewModel
import com.kekulta.androidband.formRationale
import com.kekulta.androidband.presentation.ui.events.ControlType
import com.kekulta.androidband.showHelpPage
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MicRecordingControlFragment : Fragment() {
    private var _binding: FragmentMicRecordingBinding? = null
    private val binding: FragmentMicRecordingBinding get() = requireNotNull(_binding)
    private val viewModel: MainFragmentViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMicRecordingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.interfaceState.collect { interfaceState ->
                with(binding) {
                    with(interfaceState) {
                        micRecordingButton.bind(
                            micRecordingButtonState,
                            R.drawable.baseline_fiber_manual_record_24,
                            R.drawable.baseline_stop_24
                        )
                    }
                }
            }
        }

        binding.micRecordingButton.setOnClickListener {
            if (!viewModel.isMicRecording()) {
                //TODO text
                val micRecordingRationale = requireContext().formRationale(
                    title = "We care about privacy",
                    message = "We do not record you without your consent but for audio capturing your track we need recording permission and screencast permission.",
                )

                viewModel.startMicRecording(micRecordingRationale)
            } else {
                viewModel.endMicRecording()
            }
        }

        binding.leftArrowButton.setOnClickListener {
            viewModel.goToControl(ControlType.CAPTURING)
        }

        binding.rightArrowButton.setOnClickListener {
            viewModel.goToControl(ControlType.RECORDING)
        }

        // TODO text
        binding.helpButton.setOnClickListener {
            requireContext().showHelpPage("Mic recording mode", R.layout.mic_recording_help_page)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}