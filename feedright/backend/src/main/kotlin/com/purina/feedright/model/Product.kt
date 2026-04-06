package com.purina.feedright.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val sku: String,  // Unique product SKU

    @Column(nullable = false)
    val name: String,  // Display name (e.g., "Purina Pro Pig Grower 16%")

    @Column(nullable = false)
    val category: String,  // "pig", "cattle", "poultry", etc.

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    val visits: List<Visit> = emptyList()
)
