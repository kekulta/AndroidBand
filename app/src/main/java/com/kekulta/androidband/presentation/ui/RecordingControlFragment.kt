package com.kekulta.androidband.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kekulta.androidband.R
import com.kekulta.androidband.bind
import com.kekulta.androidband.databinding.FragmentRecordingBinding
import com.kekulta.androidband.domain.viewmodels.MainFragmentViewModel
import com.kekulta.androidband.presentation.ui.dialogs.showHelpPage
import com.kekulta.androidband.presentation.ui.dialogs.showProgress
import com.kekulta.androidband.presentation.ui.events.ControlType
import com.kekulta.androidband.snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RecordingControlFragment : Fragment() {
    private var _binding: FragmentRecordingBinding? = null
    private val binding: FragmentRecordingBinding get() = requireNotNull(_binding)
    private val viewModel: MainFragmentViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.interfaceState.collect { interfaceState ->
                with(binding) {
                    with(interfaceState) {
                        recordingButton.bind(
                            recordButtonState,
                            R.drawable.baseline_fiber_manual_record_24,
                            R.drawable.baseline_stop_24
                        )
                        playRecordButton.bind(
                            recordPlayButtonState,
                            R.drawable.baseline_play_circle_24,
                            R.drawable.baseline_pause_circle_24
                        )
                        renderRecordButton.bind(renderButtonState)
                    }
                }
            }
        }

        binding.recordingButton.setOnClickListener { viewModel.onRecordingClick() }
        binding.playRecordButton.setOnClickListener { viewModel.onRecordingPlayClick() }


        binding.renderRecordButton.setOnClickListener {
            requireContext().showProgress(getString(R.string.rendering_progress_title)) {
                val result = viewModel.renderRecord()
                if (result == null) {
                    binding.root.snackbar(getString(R.string.rendering_failure_snackbar))
                } else {
                    binding.root.snackbar(
                        getString(R.string.rendering_success_snackbar).format(
                            result.name
                        )
                    )
                }
            }
        }

        binding.prevModeButton.setOnClickListener {
            viewModel.goToControl(ControlType.MIC_RECORDING)
        }

        binding.helpButton.setOnClickListener {
            requireContext().showHelpPage(
                getString(R.string.recording_help_page_title),
                R.layout.recording_help_page
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}