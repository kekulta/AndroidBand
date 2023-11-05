package com.kekulta.androidband.domain.audio.sounds

import com.kekulta.androidband.R

enum class SoundType(val nameRes: Int) {
    MELODY(R.string.melody_category_name),
    DRUMS(R.string.drums_category_name),
    FX(R.string.fx_category_name),
    RECORD(R.string.records_category_name),
}