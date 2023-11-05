package com.kekulta.androidband.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuickSoundDao {
    @Query(
        """
        SELECT * FROM ${QuickSoundsEntity.TABLE_NAME} WHERE ${QuickSoundsEntity.RECORD_ID} = :recordId
    """
    )
    suspend fun getById(recordId: Int): QuickSoundsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quickSoundsEntity: QuickSoundsEntity)
}