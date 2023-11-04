package com.kekulta.androidband.presentation.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kekulta.androidband.presentation.ui.events.SoundUiEvent
import com.kekulta.androidband.presentation.ui.recycler.sounds.SoundsMenuCategory
import com.kekulta.androidband.presentation.ui.recycler.sounds.SoundsMenuCategoryAdapter

class SoundsMenuCategoryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    var eventCallback: ((SoundUiEvent) -> Unit)? = null
    private val recAdapter = SoundsMenuCategoryAdapter().apply {
        eventCallback =
            { event -> this@SoundsMenuCategoryView.eventCallback?.invoke(event) }
    }
    private val recManager = LinearLayoutManager(context)

    init {
        layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        adapter = recAdapter
        layoutManager = recManager
    }

    fun bind(category: SoundsMenuCategory) {
        recAdapter.submitList(category.sounds)
    }
}