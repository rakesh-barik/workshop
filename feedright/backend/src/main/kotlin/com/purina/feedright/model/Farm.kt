package com.purina.feedright.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "farms")
data class Farm(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val location: String,  // Text location (e.g., "Green Valley, District 5")

    @Column(nullable = false)
    val territory: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "farm", cascade = [CascadeType.ALL])
    val visits: List<Visit> = emptyList()
)
