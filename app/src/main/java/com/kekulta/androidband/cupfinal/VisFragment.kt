package com.kekulta.androidband.cupfinal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kekulta.androidband.cupfinal.VisViewType.ELLIPSE_ONE
import com.kekulta.androidband.cupfinal.VisViewType.LINES_ONE
import com.kekulta.androidband.cupfinal.VisViewType.PRISM_ONE
import com.kekulta.androidband.cupfinal.VisViewType.SPIRAL_ONE
import com.kekulta.androidband.cupfinal.VisViewType.STAR_ONE
import com.kekulta.androidband.cupfinal.VisViewType.STAR_THREE
import com.kekulta.androidband.cupfinal.VisViewType.STAR_TWO
import com.kekulta.androidband.databinding.FragmentVisBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class VisFragment : Fragment() {
    private var _binding: FragmentVisBinding? = null
    private val binding: FragmentVisBinding get() = requireNotNull(_binding)
    private val viewModel: VisViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interceptOnBackPressed()

        lifecycleScope.launch {
            viewModel.state.collect { map ->
                Log.d(LOG_TAG, map.toString())
                map.forEach { (vis, props) -> getView(vis).visualize(props) }
            }
        }

        lifecycleScope.launch {
            viewModel.samples.collect { samples ->
                if (samples.isBlank()) {
                    binding.sampleNameTv.visibility = View.GONE
                } else {
                    binding.sampleNameTv.apply {
                        text = samples
                        binding.sampleNameTv.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.buttonState.collect {
                binding.pauseButton.isEnabled = it
            }
        }

        binding.pauseButton.setOnClickListener {
            viewModel.onPauseClicked()
        }
    }

    private fun getView(vis: VisViewType): VisualizerView =
        with(binding) {
            when (vis) {
                ELLIPSE_ONE -> ellipseOne
                STAR_ONE -> starOne
                STAR_TWO -> starTwo
                STAR_THREE -> starThree
                LINES_ONE -> linesOne
                SPIRAL_ONE -> spiralOne
                PRISM_ONE -> prismOne
            }
        }

    private fun interceptOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {
            parentFragmentManager.popBackStack()
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        const val LOG_TAG = "VisFragment"
    }
}

const val VIS_PERIOD_MILLIS = 200L

enum class VisViewType {
    ELLIPSE_ONE,
    STAR_ONE,
    STAR_TWO,
    STAR_THREE,
    LINES_ONE,
    SPIRAL_ONE,
    PRISM_ONE
}