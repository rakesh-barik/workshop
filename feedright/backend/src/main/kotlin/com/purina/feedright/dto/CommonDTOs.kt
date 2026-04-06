package com.purina.feedright.dto

import java.time.LocalDateTime
import java.util.UUID

// Salesman DTOs
data class SalesmanDTO(
    val id: UUID,
    val name: String,
    val phone: String,
    val territory: String
)

// Farm DTOs
data class FarmDTO(
    val id: UUID,
    val name: String,
    val location: String,
    val territory: String
)

data class FarmCreateRequest(
    val name: String,
    val location: String,
    val territory: String
)

// Product DTOs
data class ProductDTO(
    val id: UUID,
    val sku: String,
    val name: String,
    val category: String,
    val isActive: Boolean
)

// Auth DTOs
data class AuthRequest(
    val phone: String,
    val pin: String
)

data class AuthResponse(
    val token: String,
    val salesman: SalesmanDTO
)

// Generic responses
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
