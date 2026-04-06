package com.purina.feedright.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "visits", indexes = [
    Index(name = "idx_visit_date", columnList = "visit_date"),
    Index(name = "idx_device_created", columnList = "device_id,created_at")
])
data class Visit(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesman_id", nullable = false)
    val salesman: Salesman,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    val farm: Farm,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(nullable = false)
    val quantity: Double,  // Quantity in kg or bags

    @Column(name = "visit_date", nullable = false)
    val visitDate: LocalDateTime,

    @Column(length = 500)
    val notes: String? = null,  // Optional notes

    @Column(name = "device_id", nullable = false)
    val deviceId: String,  // For idempotency and conflict resolution

    @Column(name = "synced_at")
    val syncedAt: LocalDateTime? = null,  // When it reached backend

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()  // When salesman logged it
)
