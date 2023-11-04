package com.kekulta.androidband.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SoundDao {
    @Query(
        """
        SELECT * FROM ${SoundEntity.TABLE_NAME} WHERE ${SoundEntity.FILE_NAME} = :fileName 
    """
    )
    suspend fun getByFileName(fileName: String): SoundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SoundEntity)

    @Query(
        """
            DELETE FROM ${SoundEntity.TABLE_NAME} WHERE ${SoundEntity.FILE_NAME} = :fileName 
        """
    )
    suspend fun deleteByFileName(fileName: String)
}