package com.purina.feedright.data.remote

import retrofit2.http.*

interface FeedRightApi {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @GET("visits")
    suspend fun getVisits(): List<VisitDto>

    @GET("farms")
    suspend fun getFarms(): List<FarmDto>

    @GET("products")
    suspend fun getProducts(@Query("activeOnly") activeOnly: Boolean = true): List<ProductDto>

    @POST("visits/sync")
    suspend fun syncVisits(@Body request: SyncRequest): SyncResponse
}

// DTOs
data class AuthRequest(val phone: String, val pin: String)
data class AuthResponse(val token: String, val salesman: SalesmanDto)

data class SalesmanDto(
    val id: String,
    val name: String,
    val phone: String,
    val territory: String
)

data class FarmDto(
    val id: String,
    val name: String,
    val location: String,
    val territory: String
)

data class ProductDto(
    val id: String,
    val sku: String,
    val name: String,
    val category: String,
    val isActive: Boolean
)

data class VisitDto(
    val id: String,
    val salesmanId: String,
    val farmId: String,
    val productId: String,
    val quantity: Double,
    val visitDate: String,
    val notes: String?,
    val deviceId: String,
    val createdAt: String,
    val syncedAt: String?
)

data class VisitCreateRequest(
    val salesmanId: String,
    val farmId: String,
    val productId: String,
    val quantity: Double,
    val notes: String?,
    val visitDate: String,
    val deviceId: String,
    val createdAt: String
)

data class SyncRequest(val visits: List<VisitCreateRequest>)
data class SyncResponse(
    val synced: Int,
    val failed: Int,
    val errors: List<String>
)
