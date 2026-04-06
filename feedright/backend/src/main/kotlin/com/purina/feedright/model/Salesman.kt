package com.purina.feedright.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "salesmen")
data class Salesman(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val phone: String,

    @Column(nullable = false)
    val territory: String,

    @Column(nullable = false)
    val pin: String,  // Hashed PIN for authentication

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "salesman", cascade = [CascadeType.ALL])
    val visits: List<Visit> = emptyList()
)
