package com.kekulta.androidband.presentation.ui.recycler.sounds

import com.kekulta.androidband.domain.audio.sounds.SoundType
import com.kekulta.androidband.presentation.ui.vo.SoundVo

data class SoundsMenuCategory(val category: SoundType, val sounds: List<SoundVo>)