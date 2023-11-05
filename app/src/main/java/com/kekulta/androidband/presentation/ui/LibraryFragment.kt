package com.kekulta.androidband.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.kekulta.androidband.App
import com.kekulta.androidband.R
import com.kekulta.androidband.databinding.FragmentLibraryBinding
import com.kekulta.androidband.domain.audio.sounds.SoundType.DRUMS
import com.kekulta.androidband.domain.audio.sounds.SoundType.MELODY
import com.kekulta.androidband.domain.audio.sounds.SoundType.RECORD
import com.kekulta.androidband.domain.viewmodels.LibraryFragmentViewModel
import com.kekulta.androidband.presentation.ui.dialogs.showInput
import com.kekulta.androidband.presentation.ui.events.LibraryFragmentEvent
import com.kekulta.androidband.presentation.ui.viewpager.LibraryPagerAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File


class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding: FragmentLibraryBinding get() = requireNotNull(_binding)
    private val viewModel: LibraryFragmentViewModel by activityViewModel()
    private val category: Int? by lazy { arguments?.getInt(CATEGORY_KEY) }
    private var isRecreated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isRecreated = savedInstanceState?.getBoolean(RECREATED_KEY) ?: false


        interceptOnBackPressed()
        bindPager()
        bindEventsChannel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(RECREATED_KEY, true)
    }

    private fun interceptOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {
            parentFragmentManager.popBackStack()
        }

        binding.backButton.setOnClickListener {
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

    private fun bindPager() {
        binding.libraryPager.adapter = LibraryPagerAdapter(this)
        TabLayoutMediator(binding.libraryTabLayout, binding.libraryPager) { tab, position ->
            tab.text = when (viewModel.state.value[position].category) {
                MELODY -> getString(R.string.melody_category_name)
                DRUMS -> getString(R.string.drums_category_name)
                RECORD -> getString(R.string.records_category_name)
            }
        }.attach()

        category?.let { category ->
            if (!isRecreated) {
                binding.libraryPager.setCurrentItem(category, false)
            }
        }
    }

    private fun getInput(inputId: Int, title: String, message: String) {
        requireContext().showInput(title, message) { input ->
            viewModel.onInputResult(inputId, input)
        }
    }

    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(requireContext(), App.AUDIO_PROVIDER_AUTHORITY, file)

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

    companion object {
        private const val RECREATED_KEY = "recreated_key"
        private const val CATEGORY_KEY = "category_key"

        fun newInstance(categoryNum: Int?): LibraryFragment {
            return LibraryFragment().apply { arguments = bundleOf(CATEGORY_KEY to categoryNum) }
        }
    }
}