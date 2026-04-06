package com.purina.feedright.controller

import com.purina.feedright.dto.ProductDTO
import com.purina.feedright.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getAllProducts(@RequestParam(required = false) activeOnly: Boolean = false): ResponseEntity<List<ProductDTO>> {
        val products = if (activeOnly) {
            productService.getActiveProducts()
        } else {
            productService.getAllProducts()
        }
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: UUID): ResponseEntity<ProductDTO> {
        val product = productService.getProductById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(product)
    }

    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String): ResponseEntity<List<ProductDTO>> {
        return ResponseEntity.ok(productService.getProductsByCategory(category))
    }
}
