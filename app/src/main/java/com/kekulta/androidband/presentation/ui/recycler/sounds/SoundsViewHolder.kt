package com.kekulta.androidband.presentation.ui.recycler.sounds

import androidx.recyclerview.widget.RecyclerView
import com.kekulta.androidband.presentation.ui.customviews.SoundView
import com.kekulta.androidband.presentation.ui.vo.SoundVo

class SoundsViewHolder(
    val view: SoundView
) : RecyclerView.ViewHolder(view) {

    fun bind(vo: SoundVo) {
        view.bind(vo)
    }
}