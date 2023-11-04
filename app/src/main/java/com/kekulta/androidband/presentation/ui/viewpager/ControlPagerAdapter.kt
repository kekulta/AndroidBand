package com.kekulta.androidband.presentation.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kekulta.androidband.presentation.ui.CapturingControlFragment
import com.kekulta.androidband.presentation.ui.MicRecordingControlFragment
import com.kekulta.androidband.presentation.ui.RecordingControlFragment

class ControlPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CapturingControlFragment()
            1 -> MicRecordingControlFragment()
            2 -> RecordingControlFragment()
            else -> throw IllegalStateException("There are only two control fragments")
        }
    }
}