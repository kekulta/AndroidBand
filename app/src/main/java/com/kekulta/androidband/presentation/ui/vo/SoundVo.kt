package com.kekulta.androidband.presentation.ui.vo

import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import com.kekulta.androidband.domain.audio.sounds.SoundType

data class SoundVo(
    val soundId: Int,
    val name: String,
    @DrawableRes val icon: Int,
    @MenuRes val menu: Int,
    val type: SoundType,
    val checked: Boolean,
)