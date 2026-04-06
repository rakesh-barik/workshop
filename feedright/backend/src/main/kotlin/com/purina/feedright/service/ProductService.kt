package com.purina.feedright.service

import com.purina.feedright.dto.ProductDTO
import com.purina.feedright.model.Product
import com.purina.feedright.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ProductService(private val productRepository: ProductRepository) {

    fun getAllProducts(): List<ProductDTO> {
        return productRepository.findAll().map { it.toDTO() }
    }

    fun getActiveProducts(): List<ProductDTO> {
        return productRepository.findByIsActive(true).map { it.toDTO() }
    }

    fun getProductsByCategory(category: String): List<ProductDTO> {
        return productRepository.findByCategoryAndIsActive(category, true).map { it.toDTO() }
    }

    fun getProductById(id: UUID): ProductDTO? {
        return productRepository.findById(id).map { it.toDTO() }.orElse(null)
    }

    private fun Product.toDTO() = ProductDTO(
        id = this.id!!,
        sku = this.sku,
        name = this.name,
        category = this.category,
        isActive = this.isActive
    )
}
