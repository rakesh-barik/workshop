package com.purina.feedright.dto

import java.time.LocalDateTime
import java.util.UUID

// Response DTO for Visit
data class VisitDTO(
    val id: UUID,
    val salesmanId: UUID,
    val salesmanName: String,
    val farmId: UUID,
    val farmName: String,
    val productId: UUID,
    val productName: String,
    val productSku: String,
    val quantity: Double,
    val visitDate: LocalDateTime,
    val notes: String?,
    val createdAt: LocalDateTime,
    val syncedAt: LocalDateTime?
)

// Request DTO for creating a visit
data class VisitCreateRequest(
    val salesmanId: UUID,
    val farmId: UUID,
    val productId: UUID,
    val quantity: Double,
    val visitDate: LocalDateTime,
    val notes: String? = null,
    val deviceId: String,
    val createdAt: LocalDateTime
)

// Batch sync request from mobile
data class SyncRequest(
    val visits: List<VisitCreateRequest>
)

// Sync response
data class SyncResponse(
    val synced: Int,
    val failed: Int,
    val errors: List<String> = emptyList()
)
