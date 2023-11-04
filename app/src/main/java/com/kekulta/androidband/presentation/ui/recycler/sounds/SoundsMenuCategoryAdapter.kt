package com.kekulta.androidband.presentation.ui.recycler.sounds

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kekulta.androidband.presentation.ui.customviews.SoundView
import com.kekulta.androidband.presentation.ui.events.SoundUiEvent
import com.kekulta.androidband.presentation.ui.vo.SoundVo

class SoundsMenuCategoryAdapter : ListAdapter<SoundVo, SoundsViewHolder>(diffCallback) {
    var eventCallback: ((SoundUiEvent) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundsViewHolder {
        return SoundsViewHolder(SoundView(parent.context))
    }

    override fun onBindViewHolder(holder: SoundsViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.view.eventCallback = { event ->
            eventCallback?.invoke(event)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SoundVo>() {
            override fun areItemsTheSame(oldItem: SoundVo, newItem: SoundVo): Boolean {
                return oldItem.soundId == newItem.soundId
            }

            override fun areContentsTheSame(oldItem: SoundVo, newItem: SoundVo): Boolean {
                return oldItem == newItem
            }

        }
    }
}