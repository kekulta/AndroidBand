package com.kekulta.androidband.presentation.ui.vo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kekulta.androidband.databinding.FragmentLibraryPageBinding
import com.kekulta.androidband.domain.viewmodels.LibraryFragmentViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class LibraryPageFragment : Fragment() {
    private var _binding: FragmentLibraryPageBinding? = null
    private val binding: FragmentLibraryPageBinding get() = requireNotNull(_binding)
    private val viewModel: LibraryFragmentViewModel by activityViewModel()
    private val categoryNum: Int by lazy { requireNotNull(requireArguments().getInt(CATEGORY_KEY)) { "LibraryPageFragment with category number!" } }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pageView.eventCallback = viewModel::onSoundUiEvent

        lifecycleScope.launch {
            viewModel.state.collect { categories ->
                binding.pageView.bind(categories[categoryNum])
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val CATEGORY_KEY = "CATEGORY_KEY"

        fun newInstance(categoryNum: Int): LibraryPageFragment {
            return LibraryPageFragment().apply { arguments = bundleOf(CATEGORY_KEY to categoryNum) }
        }
    }
}