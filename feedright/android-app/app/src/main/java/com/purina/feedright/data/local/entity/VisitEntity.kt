package com.purina.feedright.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "visits")
data class VisitEntity(
    @PrimaryKey
    val id: String,
    val salesmanId: String,
    val farmId: String,
    val productId: String,
    val quantity: Double,
    val visitDate: String, // ISO 8601 format
    val notes: String?,
    val deviceId: String,
    val createdAt: String, // ISO 8601 format
    val syncedAt: String?, // Null if not synced yet
    val isSynced: Boolean = false
)
