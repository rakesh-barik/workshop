package com.purina.feedright.data.repository

import com.purina.feedright.data.local.dao.FarmDao
import com.purina.feedright.data.local.entity.FarmEntity
import com.purina.feedright.data.remote.FeedRightApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Farm data
 * Single source of truth: Room database
 */
@Singleton
class FarmRepository @Inject constructor(
    private val farmDao: FarmDao,
    private val api: FeedRightApi
) {

    /**
     * Get all farms from local database
     */
    fun getAllFarms(): Flow<List<FarmEntity>> {
        return farmDao.getAllFarms()
    }

    /**
     * Get farms by territory
     */
    fun getFarmsByTerritory(territory: String): Flow<List<FarmEntity>> {
        return farmDao.getFarmsByTerritory(territory)
    }

    /**
     * Sync farms from server to local database
     */
    suspend fun syncFarms(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val farms = api.getFarms()

            // Convert DTOs to entities
            val farmEntities = farms.map { dto ->
                FarmEntity(
                    id = dto.id,
                    name = dto.name,
                    location = dto.location,
                    territory = dto.territory
                )
            }

            // Replace all farms with fresh data from server
            farmDao.deleteAll()
            farmDao.insertAll(farmEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if farms need to be synced (empty database)
     */
    suspend fun needsSync(): Boolean = withContext(Dispatchers.IO) {
        farmDao.getCount() == 0
    }
}
