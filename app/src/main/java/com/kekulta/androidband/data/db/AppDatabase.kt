package com.kekulta.androidband.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SoundEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun soundsDao(): SoundDao

    companion object {
        fun createDatabase(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()
        }
    }
}
