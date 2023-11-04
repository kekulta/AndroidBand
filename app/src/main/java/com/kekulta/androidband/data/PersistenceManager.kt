package com.kekulta.androidband.data

import com.kekulta.androidband.data.db.SampleDao
import com.kekulta.androidband.data.db.SampleEntity
import com.kekulta.androidband.domain.audio.samples.SampleManager
import com.kekulta.androidband.domain.viewmodels.AbstractCoroutineManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class PersistenceManager(
    private val sampleDao: SampleDao,
    private val sampleManager: SampleManager,
    private val soundsDataStore: SoundsDataStore,
) : AbstractCoroutineManager() {

    fun load() {
        launchScope(this) {
            soundsDataStore.updateSounds()
            withContext(Dispatchers.IO) {
                sampleDao.getAll().forEach { entity ->
                    sampleManager.addSample(entity.soundId, entity.name, entity.sampleId)
                    ensureActive()
                }
            }
        }
    }

    fun save() {
        launchScope(this) {
            withContext(Dispatchers.IO) {
                sampleDao.deleteAll()
                sampleManager.getAllSamples().mapIndexed { index, sampleVo ->
                    SampleEntity(
                        sampleVo.sampleId,
                        sampleVo.soundId,
                        sampleVo.name,
                        index
                    )
                }.let { samples -> sampleDao.insertAll(samples) }
            }
        }
    }
}