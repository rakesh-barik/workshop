package com.purina.feedright.controller

import com.purina.feedright.dto.SalesmanDTO
import com.purina.feedright.service.SalesmanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/salesmen")
class SalesmanController(private val salesmanService: SalesmanService) {

    @GetMapping
    fun getAllSalesmen(): ResponseEntity<List<SalesmanDTO>> {
        return ResponseEntity.ok(salesmanService.getAllSalesmen())
    }

    @GetMapping("/{id}")
    fun getSalesmanById(@PathVariable id: UUID): ResponseEntity<SalesmanDTO> {
        val salesman = salesmanService.getSalesmanById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(salesman)
    }

    @GetMapping("/territory/{territory}")
    fun getSalesmenByTerritory(@PathVariable territory: String): ResponseEntity<List<SalesmanDTO>> {
        return ResponseEntity.ok(salesmanService.getSalesmenByTerritory(territory))
    }
}
