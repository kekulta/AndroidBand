package com.kekulta.androidband.presentation.ui.recycler.samples

import androidx.recyclerview.widget.RecyclerView
import com.kekulta.androidband.presentation.ui.customviews.SampleView
import com.kekulta.androidband.presentation.ui.vo.SampleVo

class SampleViewHolder(val view: SampleView) :
    RecyclerView.ViewHolder(view) {
    fun bind(vo: SampleVo) {
        view.bind(vo)
    }
}