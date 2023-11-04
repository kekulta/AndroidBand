package com.kekulta.androidband.presentation.ui.recycler.samples

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kekulta.androidband.presentation.ui.customviews.SampleView
import com.kekulta.androidband.presentation.ui.events.SampleUiEvent
import com.kekulta.androidband.presentation.ui.vo.SampleVo

class SamplesRecyclerAdapter : ListAdapter<SampleVo, SampleViewHolder>(diffCallback) {
    var eventCallback: ((SampleUiEvent) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(SampleView(parent.context))
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.view.eventCallback =
            { event -> eventCallback?.invoke(event) }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SampleVo>() {
            override fun areItemsTheSame(oldItem: SampleVo, newItem: SampleVo): Boolean {
                return oldItem.sampleId == newItem.sampleId
            }

            override fun areContentsTheSame(oldItem: SampleVo, newItem: SampleVo): Boolean {
                return oldItem == newItem
            }

        }
    }
}