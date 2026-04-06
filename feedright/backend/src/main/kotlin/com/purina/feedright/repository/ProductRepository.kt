package com.purina.feedright.repository

import com.purina.feedright.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductRepository : JpaRepository<Product, UUID> {
    fun findBySku(sku: String): Product?
    fun findByCategoryAndIsActive(category: String, isActive: Boolean = true): List<Product>
    fun findByIsActive(isActive: Boolean): List<Product>
}
