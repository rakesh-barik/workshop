package com.purina.feedright.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val sku: String,
    val name: String,
    val category: String,
    val isActive: Boolean
)
