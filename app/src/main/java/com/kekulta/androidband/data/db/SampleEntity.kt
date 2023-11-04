package com.kekulta.androidband.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kekulta.androidband.data.db.SampleEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME
)
data class SampleEntity(
    @PrimaryKey
    @ColumnInfo(name = SAMPLE_ID)
    val sampleId: Int,
    @ColumnInfo(name = SOUND_ID)
    val soundId: Int,
    @ColumnInfo(name = NAME)
    val name: String,
    @ColumnInfo(name = ORDINAL)
    val ordinal: Int,
) {
    companion object {
        const val TABLE_NAME = "SAMPLE_TABLE"
        const val SAMPLE_ID = "SAMPLE_ID_COLUMN"
        const val SOUND_ID = "SOUND_ID_COLUMN"
        const val ORDINAL = "SAMPLE_ORDINAL"
        const val NAME = "SAMPLE_NAME"
    }
}

