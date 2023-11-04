package com.kekulta.androidband.presentation.ui.recycler.sounds

import androidx.recyclerview.widget.RecyclerView
import com.kekulta.androidband.presentation.ui.customviews.SoundsMenuCategoryView

class SoundsMenuCategoryViewHolder(val view: SoundsMenuCategoryView) :
    RecyclerView.ViewHolder(view) {

    fun bind(category: SoundsMenuCategory) {
        view.bind(category)
    }
}
