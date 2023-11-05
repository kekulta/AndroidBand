package com.kekulta.androidband.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SoundEntity::class, SampleEntity::class, QuickSoundsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSoundDao(): SoundDao
    abstract fun getSamplesDao(): SampleDao
    abstract fun getQuickSoundDao(): QuickSoundDao

    companion object {
        fun createDatabase(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()
        }
    }
}
