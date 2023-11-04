package com.kekulta.androidband.presentation.ui.recycler.waves

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kekulta.androidband.presentation.ui.customviews.WaveUnitView
import com.kekulta.androidband.presentation.ui.vo.WaveUnitVo

class WaveFormRecyclerAdapter : ListAdapter<WaveUnitVo, WaveUnitViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaveUnitViewHolder {
        return WaveUnitViewHolder(WaveUnitView(parent.context))
    }

    override fun onBindViewHolder(holder: WaveUnitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<WaveUnitVo>() {
            override fun areItemsTheSame(oldItem: WaveUnitVo, newItem: WaveUnitVo): Boolean {
                return oldItem.waveId == newItem.waveId
            }

            override fun areContentsTheSame(oldItem: WaveUnitVo, newItem: WaveUnitVo): Boolean {
                return newItem == oldItem
            }
        }
    }
}