package com.kekulta.androidband.presentation.ui.recycler.sounds

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kekulta.androidband.presentation.ui.customviews.SoundsMenuCategoryView
import com.kekulta.androidband.presentation.ui.events.SoundUiEvent

class SoundsMenuAdapter :
    ListAdapter<SoundsMenuCategory, SoundsMenuCategoryViewHolder>(diffCallback) {
    var eventCallback: ((SoundUiEvent) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SoundsMenuCategoryViewHolder {
        return SoundsMenuCategoryViewHolder(SoundsMenuCategoryView(parent.context))
    }

    override fun onBindViewHolder(holder: SoundsMenuCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.view.eventCallback = { event ->
            eventCallback?.invoke(event)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SoundsMenuCategory>() {
            override fun areItemsTheSame(
                oldItem: SoundsMenuCategory,
                newItem: SoundsMenuCategory
            ): Boolean {
                return oldItem.category == newItem.category
            }

            override fun areContentsTheSame(
                oldItem: SoundsMenuCategory,
                newItem: SoundsMenuCategory
            ): Boolean {
                return oldItem.sounds == newItem.sounds
            }
        }
    }
}