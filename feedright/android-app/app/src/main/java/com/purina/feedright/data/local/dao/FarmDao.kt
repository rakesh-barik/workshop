package com.purina.feedright.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.purina.feedright.data.local.entity.FarmEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Farm entities
 */
@Dao
interface FarmDao {

    @Query("SELECT * FROM farms ORDER BY name ASC")
    fun getAllFarms(): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE territory = :territory ORDER BY name ASC")
    fun getFarmsByTerritory(territory: String): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE id = :farmId")
    suspend fun getFarmById(farmId: String): FarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(farms: List<FarmEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(farm: FarmEntity)

    @Query("DELETE FROM farms")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM farms")
    suspend fun getCount(): Int
}
