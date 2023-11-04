package com.kekulta.androidband.domain.audio.sounds

import android.net.Uri
import androidx.annotation.RawRes

sealed class Sound(
    open val name: String,
    open val soundId: Int,
    open val type: SoundType,
) {
    data class Asset(
        override val name: String,
        override val soundId: Int,
        override val type: SoundType,
        @RawRes val resId: Int,

        ) : Sound(name, soundId, type)

    data class Record(
        override val name: String,
        override val soundId: Int,
        val uri: Uri,
    ) : Sound(name, soundId, SoundType.RECORD)
}