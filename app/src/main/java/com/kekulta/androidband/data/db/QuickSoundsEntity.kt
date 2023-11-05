package com.kekulta.androidband.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kekulta.androidband.data.db.QuickSoundsEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class QuickSoundsEntity(
    @PrimaryKey @ColumnInfo(name = RECORD_ID) val recordId: Int,
    @ColumnInfo(name = SOUND_ID_ONE) val soundIdOne: Int,
    @ColumnInfo(name = SOUND_ID_TWO) val soundIdTwo: Int,
    @ColumnInfo(name = SOUND_ID_THREE) val soundIdThree: Int,
    @ColumnInfo(name = SOUND_ID_FOUR) val soundIdFour: Int,
) {
    companion object {
        const val TABLE_NAME = "QUICKSOUND_TABLE"
        const val RECORD_ID_VALUE = 0
        const val RECORD_ID = "RECORD_ID"
        const val SOUND_ID_ONE = "SOUND_ID_ONE"
        const val SOUND_ID_TWO = "SOUND_ID_TWO"
        const val SOUND_ID_THREE = "SOUND_ID_THREE"
        const val SOUND_ID_FOUR = "SOUND_ID_FOUR"
    }
}