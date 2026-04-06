package com.purina.feedright.data.repository

import com.purina.feedright.data.local.dao.ProductDao
import com.purina.feedright.data.local.entity.ProductEntity
import com.purina.feedright.data.remote.FeedRightApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Product data
 * Single source of truth: Room database
 */
@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val api: FeedRightApi
) {

    /**
     * Get all active products from local database
     */
    fun getActiveProducts(): Flow<List<ProductEntity>> {
        return productDao.getActiveProducts()
    }

    /**
     * Get products by category
     */
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> {
        return productDao.getProductsByCategory(category)
    }

    /**
     * Sync products from server to local database
     */
    suspend fun syncProducts(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val products = api.getProducts()

            // Convert DTOs to entities
            val productEntities = products.map { dto ->
                ProductEntity(
                    id = dto.id,
                    sku = dto.sku,
                    name = dto.name,
                    category = dto.category,
                    isActive = dto.isActive
                )
            }

            // Replace all products with fresh data from server
            productDao.deleteAll()
            productDao.insertAll(productEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if products need to be synced (empty database)
     */
    suspend fun needsSync(): Boolean = withContext(Dispatchers.IO) {
        productDao.getActiveCount() == 0
    }
}
