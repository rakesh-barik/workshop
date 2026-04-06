package com.purina.feedright.data.local.dao

import androidx.room.*
import com.purina.feedright.data.local.entity.VisitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {
    @Query("SELECT * FROM visits ORDER BY visitDate DESC LIMIT 10")
    fun getRecentVisits(): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE isSynced = 0")
    suspend fun getUnsyncedVisits(): List<VisitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(visit: VisitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: VisitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisits(visits: List<VisitEntity>)

    @Query("SELECT COUNT(*) FROM visits WHERE isSynced = 0")
    suspend fun getUnsyncedCount(): Int

    @Update
    suspend fun updateVisit(visit: VisitEntity)

    @Query("UPDATE visits SET isSynced = 1, syncedAt = :syncedAt WHERE id = :visitId")
    suspend fun markAsSynced(visitId: String, syncedAt: String)

    @Query("DELETE FROM visits")
    suspend fun deleteAllVisits()
}
