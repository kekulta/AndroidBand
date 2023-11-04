package com.kekulta.androidband.presentation.ui.recycler.waves

import androidx.recyclerview.widget.RecyclerView
import com.kekulta.androidband.presentation.ui.customviews.WaveUnitView
import com.kekulta.androidband.presentation.ui.vo.WaveUnitVo

class WaveUnitViewHolder(val view: WaveUnitView) : RecyclerView.ViewHolder(view) {
    fun bind(vo: WaveUnitVo) {
        view.bind(vo)
    }
}