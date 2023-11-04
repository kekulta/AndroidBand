package com.kekulta.androidband.presentation.framework

import android.content.Context
import androidx.core.net.toFile
import com.kekulta.androidband.copyInputStreamToFile
import com.kekulta.androidband.data.SoundsDataStore
import com.kekulta.androidband.domain.audio.sounds.Sound
import java.io.File

class AssetManager(
    private val filesManager: FilesManager,
    private val context: Context,
    private val soundsDataStore: SoundsDataStore,
) {

    fun unpackAsset(soundId: Int): File {
        val dest: File
        when (val sound = soundsDataStore.getById(soundId)) {
            is Sound.Asset -> {
                with(context) {
                    dest = File("${filesManager.assetDir}/${sound.name}.wav")
                    val stream = resources.openRawResource(sound.resId)
                    dest.delete()
                    dest.createNewFile()
                    copyInputStreamToFile(stream, dest)
                }
            }

            is Sound.Record -> {
                dest = File("${filesManager.assetDir}/${sound.soundId}.wav")
                sound.uri.toFile().copyTo(dest, true)
            }
        }
        return dest
    }
}