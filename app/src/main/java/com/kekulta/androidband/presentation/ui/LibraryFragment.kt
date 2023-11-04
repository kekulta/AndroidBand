package com.kekulta.androidband.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.kekulta.androidband.databinding.FragmentLibraryBinding
import com.kekulta.androidband.domain.viewmodels.LibraryFragmentViewModel
import com.kekulta.androidband.formInput
import com.kekulta.androidband.presentation.ui.events.LibraryFragmentEvent
import com.kekulta.androidband.presentation.ui.recycler.sounds.SoundsMenuAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding: FragmentLibraryBinding get() = requireNotNull(_binding)
    private val viewModel: LibraryFragmentViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(layoutInflater)
        interceptOnBackPressed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindRecycler()
        bindEventsChannel()
    }

    private fun interceptOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {
            parentFragmentManager.popBackStack()
        }
    }

    private fun bindEventsChannel() {
        lifecycleScope.launch {
            viewModel.eventChannel.collect { event ->
                when (event) {
                    is LibraryFragmentEvent.Share -> shareFile(event.file)
                    is LibraryFragmentEvent.Input -> getInput(
                        event.inputId,
                        event.title,
                        event.message
                    )
                }
            }
        }
    }

    private fun bindRecycler() {
        val recAdapter = SoundsMenuAdapter().apply {
            eventCallback = viewModel::onSoundUiEvent
        }
        val recManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            viewModel.state.collect { list ->
                recAdapter.submitList(list)
            }
        }

        binding.libraryRecycler.apply {
            itemAnimator = null
            adapter = recAdapter
            layoutManager = recManager
            PagerSnapHelper().attachToRecyclerView(this)
        }

    }

    private fun getInput(inputId: Int, title: String, message: String) {
        requireContext().formInput(title, message) { input ->
            viewModel.onInputResult(inputId, input)
        }
    }

    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "com.kekulta.audioprovider", file)

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "audio/wav"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}