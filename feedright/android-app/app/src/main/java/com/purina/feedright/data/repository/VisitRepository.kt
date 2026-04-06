package com.purina.feedright.data.repository

import com.purina.feedright.data.local.dao.VisitDao
import com.purina.feedright.data.local.entity.VisitEntity
import com.purina.feedright.data.remote.FeedRightApi
import com.purina.feedright.data.remote.SyncRequest
import com.purina.feedright.data.remote.VisitCreateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Visit data
 * Handles offline-first architecture with sync to server
 */
@Singleton
class VisitRepository @Inject constructor(
    private val visitDao: VisitDao,
    private val api: FeedRightApi,
    private val deviceId: String
) {

    /**
     * Get recent visits from local database
     */
    fun getRecentVisits(): Flow<List<VisitEntity>> {
        return visitDao.getRecentVisits()
    }

    /**
     * Create a new visit (saves to local database first)
     */
    suspend fun createVisit(
        salesmanId: String,
        farmId: String,
        productId: String,
        quantity: Double,
        notes: String? = null
    ): Result<VisitEntity> = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now().toString()
            val visit = VisitEntity(
                id = UUID.randomUUID().toString(),
                salesmanId = salesmanId,
                farmId = farmId,
                productId = productId,
                quantity = quantity,
                notes = notes,
                visitDate = now,
                deviceId = deviceId,
                createdAt = now,
                syncedAt = null,
                isSynced = false
            )

            visitDao.insert(visit)
            Result.success(visit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync all unsynced visits to server
     */
    suspend fun syncVisits(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val unsyncedVisits = visitDao.getUnsyncedVisits()

            if (unsyncedVisits.isEmpty()) {
                return@withContext Result.success(0)
            }

            // Convert to API request format
            val visitRequests = unsyncedVisits.map { visit ->
                VisitCreateRequest(
                    salesmanId = visit.salesmanId,
                    farmId = visit.farmId,
                    productId = visit.productId,
                    quantity = visit.quantity,
                    notes = visit.notes,
                    visitDate = visit.visitDate,
                    deviceId = visit.deviceId,
                    createdAt = visit.createdAt
                )
            }

            // Batch sync to server
            val response = api.syncVisits(SyncRequest(visitRequests))

            // Mark all attempted visits as synced in local database
            val syncedAt = Instant.now().toString()
            unsyncedVisits.forEach { visit ->
                visitDao.markAsSynced(visit.id, syncedAt)
            }

            Result.success(response.synced)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get count of unsynced visits
     */
    suspend fun getUnsyncedCount(): Int = withContext(Dispatchers.IO) {
        visitDao.getUnsyncedCount()
    }
}
