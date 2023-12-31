package com.kekulta.androidband.presentation.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kekulta.androidband.domain.audio.sounds.SoundType
import com.kekulta.androidband.presentation.ui.vo.LibraryPageFragment

class LibraryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = SoundType.entries.size

    override fun createFragment(position: Int): Fragment {
        return LibraryPageFragment.newInstance(position)
    }
}