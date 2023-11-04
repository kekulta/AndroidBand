package com.kekulta.androidband.domain.audio.samples

import androidx.annotation.RawRes

interface SamplesFactory {
    fun getSample(soundId: Int, name: String): Sample
}

