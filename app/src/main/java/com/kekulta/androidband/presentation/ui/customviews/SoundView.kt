package com.kekulta.androidband.presentation.ui.customviews

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kekulta.androidband.R
import com.kekulta.androidband.databinding.SoundViewBinding
import com.kekulta.androidband.domain.audio.sounds.SoundType
import com.kekulta.androidband.getColorStateList
import com.kekulta.androidband.getDrawable
import com.kekulta.androidband.presentation.ui.events.SoundUiEvent
import com.kekulta.androidband.presentation.ui.vo.SoundVo

class SoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding: SoundViewBinding =
        SoundViewBinding.inflate(LayoutInflater.from(context), this)
    private var currId = -1
    var eventCallback: ((uiEvent: SoundUiEvent) -> Unit)? = null
    private val backgroundTint: ColorStateList?
    private val backgroundTintSelected: ColorStateList?

    init {
        layoutParams = LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

        backgroundTint = getColorStateList(R.color.sound_background_tint)
        backgroundTintSelected = getColorStateList(R.color.sound_background_tint_selected)

        binding.addButton.setOnClickListener {
            addSound()
        }

        binding.addButton.setOnLongClickListener {
            selectSound()
            true
        }

    }

    fun bind(vo: SoundVo) {
        binding.addContainer.backgroundTintList = if (vo.checked) {
            backgroundTintSelected
        } else {
            backgroundTint
        }

        currId = vo.soundId
        binding.icon.setImageDrawable(getDrawable(vo.icon))

        binding.menuButton.setOnClickListener {
            showMenu(vo.menu, binding.menuButton)
        }

        if (vo.type == SoundType.RECORD) {
            binding.soundContainer.setOnLongClickListener {
                renameSound()
                true
            }
        } else {
            binding.soundContainer.setOnLongClickListener { false }
        }

        binding.soundName.text = vo.name
    }

    private fun showMenu(@MenuRes menu: Int, v: View) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.select_quick_sound_option -> selectSound()
                R.id.share_option -> shareSound()
                R.id.delete_option -> deleteSound()
                R.id.rename_option -> renameSound()
            }
            true
        }
        popup.show()
    }

    private fun selectSound() {
        eventCallback?.invoke(SoundUiEvent.Select(currId))
    }

    private fun addSound() {
        eventCallback?.invoke(SoundUiEvent.Add(currId))
    }

    private fun shareSound() {
        eventCallback?.invoke(SoundUiEvent.Share(currId))
    }

    private fun deleteSound() {
        eventCallback?.invoke(SoundUiEvent.Delete(currId))
    }

    private fun renameSound() {
        eventCallback?.invoke(SoundUiEvent.Rename(currId))
    }

    companion object {
        const val LOG_TAG = "SoundView"
    }
}