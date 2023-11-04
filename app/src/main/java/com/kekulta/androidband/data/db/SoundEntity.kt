package com.kekulta.androidband.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kekulta.androidband.data.db.SoundEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME
)
data class SoundEntity(
    @PrimaryKey
    @ColumnInfo(name = FILE_NAME)
    val fileName: String,
    @ColumnInfo(name = SOUND_ID)
    val soundId: Int,
) {
    companion object {
        const val TABLE_NAME = "SOUND_TABLE"
        const val SOUND_ID = "SOUND_ID_COLUMN"
        const val FILE_NAME = "FILE_PATH_COLUMN"
    }
}

