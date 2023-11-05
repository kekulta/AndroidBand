package com.kekulta.androidband.data

import androidx.core.net.toFile
import androidx.core.net.toUri
import com.kekulta.androidband.R
import com.kekulta.androidband.data.db.SoundDao
import com.kekulta.androidband.data.db.SoundEntity
import com.kekulta.androidband.domain.audio.sounds.Sound
import com.kekulta.androidband.domain.audio.sounds.SoundType
import com.kekulta.androidband.domain.viewmodels.AbstractCoroutineManager
import com.kekulta.androidband.presentation.framework.FilesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import kotlin.random.Random

class SoundsDataStore(
    private val filesManager: FilesManager,
    private val soundDao: SoundDao,
) : AbstractCoroutineManager() {
    private val _sounds = MutableStateFlow<List<Sound>>(
        listOf(
            Sound.Asset("Melody One", 0, SoundType.MELODY, R.raw.sample_melody_one),
            Sound.Asset("Melody Two", 1, SoundType.MELODY, R.raw.sample_melody_two),
            Sound.Asset("Melody Three", 2, SoundType.MELODY, R.raw.sample_melody_three),
            Sound.Asset("Melody Four", 3, SoundType.MELODY, R.raw.sample_melody_four),
            Sound.Asset("Melody Five", 5, SoundType.MELODY, R.raw.sample_melody_five),
            Sound.Asset("Drums One", 6, SoundType.DRUMS, R.raw.sample_drums_one),
            Sound.Asset("Drums Two", 7, SoundType.DRUMS, R.raw.sample_drums_two),
            Sound.Asset("Drums Three", 8, SoundType.DRUMS, R.raw.sample_drums_three),
            Sound.Asset("Drums Four", 9, SoundType.DRUMS, R.raw.sample_drums_four),
            Sound.Asset("Drums Five", 10, SoundType.DRUMS, R.raw.sample_drums_five),
            Sound.Asset("Ambience D", 11, SoundType.FX, R.raw.sample_fx_ambience_d),
            Sound.Asset("Drone Reese", 12, SoundType.FX, R.raw.sample_fx_drone_reese),
            Sound.Asset("Impact Delay", 13, SoundType.FX, R.raw.sample_fx_impact_delay),
            Sound.Asset("Laugh Laser", 14, SoundType.FX, R.raw.sample_fx_laugh_laser),
            Sound.Asset("Short Sweep", 15, SoundType.FX, R.raw.sample_fx_short_sweep),
        )
    )

    val sounds: StateFlow<List<Sound>> get() = _sounds

    fun renameSound(soundId: Int, newName: String) {
        _sounds.value = _sounds.value.map { sound ->
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
                    // We do not want to finish this scope by ourselves, so start with unique key
                    launchScope(Random.nextInt()) {
                        val deletedFile = sound.uri.toFile()
                        deletedFile.delete()
                        soundDao.deleteByFileName(deletedFile.name)
                    }
                }
            }
            !isDeletedSound
        }
    }

    fun updateSounds() {
        // We do not want to finish this scope by ourselves, so start with unique key
        launchScope(Random.nextInt()) {
            updateSoundsSync()
        }
    }

    suspend fun updateSoundsSync() {
        val loadedFiles = getLoadedFiles()
        val newFiles =
            filesManager.audiosDir.listFiles { file -> file.name !in loadedFiles }.toListOrEmpty()
        addNewFiles(newFiles)
    }

    fun getById(soundId: Int): Sound {
        return _sounds.value.firstOrNull { sound -> sound.soundId == soundId }
            ?: throw IllegalArgumentException("Unknown sound!")
    }

    private suspend fun addNewFiles(newFiles: List<File>) {
        _sounds.value = _sounds.value + newFiles.map { file ->
            Sound.Record(
                file.nameWithoutExtension,
                getId(file.name),
                file.toUri(),
            )
        }
    }

    private suspend fun getId(fileName: String): Int {
        val persistedId = soundDao.getByFileName(fileName)?.soundId
        return if (persistedId == null) {
            val id = Random.nextInt()
            soundDao.insert(SoundEntity(fileName, id))
            id
        } else {
            persistedId
        }
    }

    private fun Sound.renameTo(newName: String): Sound {
        return when (this) {
            is Sound.Asset -> copy(name = newName)
            is Sound.Record -> {
                val newFile = filesManager.renameTo(uri.toFile(), newName)
                // We do not want to finish this scope by ourselves, so start with unique key
                launchScope(Random.nextInt()) {
                    soundDao.insert(SoundEntity(newFile.name, soundId))
                }
                copy(name = newName, uri = newFile.toUri())
            }
        }
    }

    private fun getLoadedFiles() = _sounds.value.filter { sound -> sound.type == SoundType.RECORD }
        .map { sound -> "${sound.name}.wav" }.toSet()

    private fun <T> Array<T>?.toListOrEmpty(): List<T> {
        return this?.toList() ?: emptyList()
    }

    companion object {
        const val LOG_TAG = "SoundsDataStore"
    }
}

