package com.kekulta.androidband.presentation.ui

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kekulta.androidband.App
import com.kekulta.androidband.R
import com.kekulta.androidband.bind
import com.kekulta.androidband.databinding.FragmentCapturingBinding
import com.kekulta.androidband.domain.viewmodels.MainFragmentViewModel
import com.kekulta.androidband.formRationale
import com.kekulta.androidband.presentation.framework.AudioCaptureService
import com.kekulta.androidband.presentation.ui.events.ControlType
import com.kekulta.androidband.showHelpPage
import com.kekulta.androidband.snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CapturingControlFragment : Fragment() {
    private var _binding: FragmentCapturingBinding? = null
    private val binding: FragmentCapturingBinding get() = requireNotNull(_binding)
    private val viewModel: MainFragmentViewModel by activityViewModel()
    private lateinit var mediaProjectionManager: MediaProjectionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCapturingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.interfaceState.collect { interfaceState ->
                with(binding) {
                    with(interfaceState) {
                        capturingButton.bind(
                            captureButtonState,
                            R.drawable.baseline_fiber_manual_record_24,
                            R.drawable.baseline_stop_24
                        )
                    }
                }
            }
        }

        binding.capturingButton.setOnClickListener {
            if (viewModel.isCapturing()) {
                stopCapturing()
            } else {
                startCapturing()
            }
        }

        binding.rightArrowButton.setOnClickListener {
            viewModel.goToControl(ControlType.MIC_RECORDING)
        }

        // TODO text
        binding.helpButton.setOnClickListener {
            requireContext().showHelpPage("Capturing mode", R.layout.capturing_help_page)
        }
    }

    private fun startCapturing() {
        //TODO text
        val capturingRationale = requireContext().formRationale(
            title = "We care about privacy",
            message = "We do not record you without your consent but for audio capturing your track we need recording permission and screencast permission.",
        )
        viewModel.withAudioPermissions(
            rationaleCallback = capturingRationale,
            forceRationale = true,
        ) { granted ->
            if (granted) {
                startMediaProjectionRequest()
            } else {
                binding.root.snackbar("Permissions to capture audio denied.")
            }
        }
    }

    private fun startMediaProjectionRequest() {
        mediaProjectionManager =
            App.instance.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        requireActivity().startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            MainActivity.MEDIA_PROJECTION_REQUEST_CODE
        )
    }

    private fun stopCapturing() {
        requireActivity().startService(
            Intent(
                requireContext(), AudioCaptureService::class.java
            ).apply {
                action = AudioCaptureService.ACTION_STOP
            })

        viewModel.onEndCapturing()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}