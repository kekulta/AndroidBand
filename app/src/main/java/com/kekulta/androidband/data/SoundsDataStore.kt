package com.kekulta.androidband.data

import androidx.core.net.toFile
import androidx.core.net.toUri
import com.kekulta.androidband.R
import com.kekulta.androidband.domain.audio.sounds.Sound
import com.kekulta.androidband.domain.audio.sounds.SoundType
import com.kekulta.androidband.presentation.framework.FilesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import kotlin.random.Random

class SoundsDataStore(
    private val filesManager: FilesManager,
) {
    private val _sounds = MutableStateFlow<List<Sound>>(
        listOf(
            Sound.Asset("melody_one", 0, SoundType.MELODY, R.raw.sample_melody_one),
            Sound.Asset("melody_two", 1, SoundType.MELODY, R.raw.sample_drums_two),
            Sound.Asset("melody_three", 2, SoundType.MELODY, R.raw.sample_melody_three),
            Sound.Asset("melody_four", 3, SoundType.MELODY, R.raw.sample_melody_four),
            Sound.Asset("drums_one", 4, SoundType.DRUMS, R.raw.sample_drums_one),
            Sound.Asset("drums_two", 5, SoundType.DRUMS, R.raw.sample_drums_two),
            Sound.Asset("drums_three", 6, SoundType.DRUMS, R.raw.sample_drums_three),
            Sound.Asset("drums_four", 7, SoundType.DRUMS, R.raw.sample_drums_four),
        )
    )

    val sounds: StateFlow<List<Sound>> get() = _sounds

    fun renameSound(soundId: Int, newName: String) {
        _sounds.value =
            _sounds.value.map { sound ->
                if (sound.soundId != soundId) sound else sound.renameTo(
                    newName
                )
            }
    }

    fun deleteSound(soundId: Int) {
        _sounds.value = _sounds.value.filter { sound ->
            val isDeletedSound = soundId == sound.soundId
            if (isDeletedSound) {
                if (sound is Sound.Record) {
                    sound.uri.toFile().delete()
                }
            }
            !isDeletedSound
        }
    }

    fun updateSounds() {
        val loadedFiles = getLoadedFiles()
        val newFiles =
            filesManager.audiosDir.listFiles { file -> file.name !in loadedFiles }.toListOrEmpty()

        addNewFiles(newFiles)
    }

    fun getById(soundId: Int): Sound {
        return _sounds.value.firstOrNull { sound -> sound.soundId == soundId }
            ?: throw IllegalArgumentException("Unknown sound!")
    }

    private fun addNewFiles(newFiles: List<File>) {
        _sounds.value = _sounds.value + newFiles.map { file ->
            Sound.Record(
                file.nameWithoutExtension,
                Random.nextInt(),
                file.toUri(),
            )
        }
    }

    private fun Sound.renameTo(newName: String): Sound {
        return when (this) {
            is Sound.Asset -> copy(name = newName)
            is Sound.Record -> {
                val newFile = filesManager.renameTo(uri.toFile(), newName)
                copy(name = newName, uri = newFile.toUri())
            }
        }
    }

    private fun getLoadedFiles() = _sounds.value
        .filter { sound -> sound.type == SoundType.RECORD }
        .map { sound -> "${sound.name}.wav" }.toSet()

    private fun <T> Array<T>?.toListOrEmpty(): List<T> {
        return this?.toList() ?: emptyList()
    }

    companion object {
        const val LOG_TAG = "SoundsDataStore"
    }
}

