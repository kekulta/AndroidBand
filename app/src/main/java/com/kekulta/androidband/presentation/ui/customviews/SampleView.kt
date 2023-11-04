package com.kekulta.androidband.presentation.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import com.kekulta.androidband.R
import com.kekulta.androidband.databinding.SampleViewBinding
import com.kekulta.androidband.disableCheck
import com.kekulta.androidband.disableDrag
import com.kekulta.androidband.getDrawable
import com.kekulta.androidband.presentation.ui.events.SampleUiEvent
import com.kekulta.androidband.presentation.ui.vo.SampleVo

class SampleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding: SampleViewBinding =
        SampleViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var isVolumeDragging = false
    private var isSpeedDragging = false
    private var currId = -1

    var eventCallback: ((event: SampleUiEvent) -> Unit)? = null


    init {
        binding.toggleButton.disableCheck()
        binding.timeSlider.disableDrag()

        binding.root.setOnLongClickListener {
            renameSample()
            true
        }

        binding.timeSlider.setOnLongClickListener {
            renameSample()
            true
        }

        binding.playButton.setOnClickListener {
            eventCallback?.invoke(SampleUiEvent.PlayClicked(currId))
        }

        binding.resetButton.setOnClickListener {
            eventCallback?.invoke(SampleUiEvent.ResetClicked(currId))
        }

        binding.soundButton.setOnClickListener {
            eventCallback?.invoke(SampleUiEvent.SoundClicked(currId))
        }

        binding.volumeSlider.apply {
            addOnSliderTouchListener(object : OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                    isVolumeDragging = true
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    isVolumeDragging = false
                }

            })

            addOnChangeListener { _, value, _ ->
                eventCallback?.invoke(SampleUiEvent.VolumeChanged(currId, value))
            }
        }

        binding.speedSlider.apply {
            addOnSliderTouchListener(object : OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                    isSpeedDragging = true
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    isSpeedDragging = false
                }
            })

            addOnChangeListener { _, value, _ ->
                eventCallback?.invoke(SampleUiEvent.SpeedChanged(currId, value))
            }
        }

        binding.menuButton.setOnClickListener {
            showMenu(binding.menuButton)
        }
    }

    fun bind(vo: SampleVo) {
        with(vo) {
            with(binding) {
                currId = sampleId
                volumeSlider.isEnabled = volumeActive
                soundButton.isEnabled = volumeActive
                speedSlider.isEnabled = speedActive
                resetButton.isEnabled = speedActive

                if (isPlaying) {
                    playButton.icon = getDrawable(R.drawable.baseline_pause_circle_24)
                } else {
                    playButton.icon = getDrawable(R.drawable.baseline_play_circle_24)
                }

                if (isSoundOn) {
                    soundButton.icon = getDrawable(R.drawable.baseline_volume_up_24)
                } else {
                    soundButton.icon = getDrawable(R.drawable.baseline_volume_off_24)
                }

                sampleName.text = name

                timeSlider.valueTo = (duration / 1000).toFloat()
                timeSlider.value = (currentPosition / 1000).toFloat()

                if (!isSpeedDragging) speedSlider.value = speed
                if (!isVolumeDragging) volumeSlider.value = volume
            }
        }
    }

    private fun showMenu(v: View) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(R.menu.sample_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.delete_option -> deleteSample()
                R.id.rename_option -> renameSample()
            }
            true
        }
        popup.show()
    }

    private fun deleteSample() {
        eventCallback?.invoke(SampleUiEvent.Delete(currId))
    }

    private fun renameSample() {
        eventCallback?.invoke(SampleUiEvent.Rename(currId))
    }

    companion object {
        const val LOG_TAG = "SampleView"
    }
}