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
import com.kekulta.androidband.presentation.ui.dialogs.formRationale
import com.kekulta.androidband.presentation.ui.dialogs.showHelpPage
import com.kekulta.androidband.presentation.ui.events.ControlType
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
                val micRecordingRationale = requireContext().formRationale(
                    title = getString(R.string.mic_recording_rationale_title),
                    message = getString(R.string.mic_recording_rationale_text),
                )

                viewModel.startMicRecording(micRecordingRationale)
            } else {
                viewModel.endMicRecording()
            }
        }

        binding.prevModeButton.setOnClickListener {
            viewModel.goToControl(ControlType.CAPTURING)
        }

        binding.nextModeButton.setOnClickListener {
            viewModel.goToControl(ControlType.RECORDING)
        }

        binding.helpButton.setOnClickListener {
            requireContext().showHelpPage(
                getString(R.string.mic_recording_help_page_title),
                R.layout.mic_recording_help_page
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}