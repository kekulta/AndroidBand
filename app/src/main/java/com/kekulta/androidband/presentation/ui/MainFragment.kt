package com.kekulta.androidband.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kekulta.androidband.R
import com.kekulta.androidband.bind
import com.kekulta.androidband.databinding.FragmentMainBinding
import com.kekulta.androidband.domain.viewmodels.MainFragmentViewModel
import com.kekulta.androidband.cupfinal.VisFragment
import com.kekulta.androidband.presentation.ui.dialogs.formRationale
import com.kekulta.androidband.presentation.ui.dialogs.showInput
import com.kekulta.androidband.presentation.ui.events.MainFragmentEvent
import com.kekulta.androidband.presentation.ui.recycler.samples.SamplesRecyclerAdapter
import com.kekulta.androidband.presentation.ui.recycler.waves.WaveFormRecyclerAdapter
import com.kekulta.androidband.presentation.ui.viewpager.ControlPagerAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = requireNotNull(_binding)
    private val viewModel: MainFragmentViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.restoreSamples()
        bindWaveRecycler()
        bindSamplesRecycler()
        bindButtons()
        bindControlPager()
        bindVisualizer()
        bindEventsChannel()
    }


    override fun onStop() {
        super.onStop()
        // This call is not guaranteed but this is OK if lost some data in crash, it is not so important
        viewModel.saveSamples()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.stopWaves()
    }

    private fun bindEventsChannel() {
        lifecycleScope.launch {
            viewModel.eventChannel.collect { event ->
                when (event) {
                    is MainFragmentEvent.Input -> getInput(
                        event.inputId, event.title, event.message
                    )
                }
            }
        }
    }

    private fun bindVisualizer() {
        val visualizerRationale = requireContext().formRationale(
            title = getString(R.string.visualizer_rationale_title),
            message = getString(R.string.visualizer_rationale_text),
        )

        viewModel.startWaves(visualizerRationale)
    }

    private fun bindControlPager() {
        val pagerAdapter = ControlPagerAdapter(this)
        binding.controlPager.adapter = pagerAdapter

        lifecycleScope.launch {
            viewModel.controlChannel.collect { control ->
                binding.controlPager.currentItem = control.ordinal
            }
        }
    }

    private fun bindButtons() {
        lifecycleScope.launch {
            viewModel.interfaceState.collect { interfaceState ->
                with(binding) {
                    with(interfaceState) {
                        sampleOne.bind(sampleOneButtonState)
                        sampleTwo.bind(sampleTwoButtonState)
                        sampleThree.bind(sampleThreeButtonState)
                        sampleFour.bind(sampleFourButtonState)
                        libraryButton.bind(libButtonState)
                    }
                }
            }
        }

        binding.sampleOne.setOnClickListener { viewModel.onQuickSampleClicked(0) }
        binding.sampleTwo.setOnClickListener { viewModel.onQuickSampleClicked(1) }
        binding.sampleThree.setOnClickListener { viewModel.onQuickSampleClicked(2) }
        binding.sampleFour.setOnClickListener { viewModel.onQuickSampleClicked(3) }

        binding.sampleOne.setOnLongClickListener { navigateToLib(0); true }
        binding.sampleTwo.setOnLongClickListener { navigateToLib(1); true }
        binding.sampleThree.setOnLongClickListener { navigateToLib(2); true }
        binding.sampleFour.setOnLongClickListener { navigateToLib(3); true }
        binding.libraryButton.setOnClickListener { navigateToLib() }
        //TODO LAND!!!!
        binding.visButton!!.setOnClickListener { navigateToVis() }

    }

    private fun navigateToVis() {
        parentFragmentManager.commit {
            addToBackStack(null)
            setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            replace(R.id.root_container, VisFragment())
        }
    }

    private fun navigateToLib(categoryNum: Int? = null) {
        viewModel.onNavigateToLib()
        parentFragmentManager.commit {
            addToBackStack(null)
            setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            replace(R.id.root_container, LibraryFragment.newInstance(categoryNum))
        }
    }

    private fun bindSamplesRecycler() {
        val samplesAdapter = SamplesRecyclerAdapter().apply {
            eventCallback = viewModel::onSampleUiEvent
        }
        val samplesManager = LinearLayoutManager(requireContext())

        binding.samplesRecycler.apply {
            adapter = samplesAdapter
            layoutManager = samplesManager
            itemAnimator = null
        }

        lifecycleScope.launch {
            viewModel.state.collect { tracks ->
                samplesAdapter.submitList(tracks)
            }
        }

    }

    private fun bindWaveRecycler() {
        val waveAdapter = WaveFormRecyclerAdapter()
        val waveManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false).apply {
                stackFromEnd = true
            }

        binding.waveRecycler.apply {
            adapter = waveAdapter
            layoutManager = waveManager
            itemAnimator = null
        }

        lifecycleScope.launch {
            viewModel.wavesState.collect { waves ->
                waveAdapter.submitList(waves)
                binding.waveRecycler.apply {
                    if (waveAdapter.itemCount > 0) {
                        post {
                            scrollToPosition(waveAdapter.itemCount - 1)
                        }
                    }
                }
            }
        }
    }

    private fun getInput(inputId: Int, title: String, message: String) {
        requireContext().showInput(title, message) { input ->
            viewModel.onInputResult(inputId, input)
        }
    }

    companion object {
        const val LOG_TAG = "MainFragment"
    }
}