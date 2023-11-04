package com.kekulta.androidband.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SampleDao {
    @Query(
        """
        DELETE FROM ${SampleEntity.TABLE_NAME}
    """
    )
    suspend fun deleteAll()

    @Query(
        """
        SELECT * FROM ${SampleEntity.TABLE_NAME} ORDER BY ${SampleEntity.ORDINAL} ASC
    """
    )
    suspend fun getAll(): List<SampleEntity>

    @Insert
    suspend fun insertAll(samples: List<SampleEntity>)
}