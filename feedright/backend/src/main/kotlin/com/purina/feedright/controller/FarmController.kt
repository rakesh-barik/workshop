package com.purina.feedright.controller

import com.purina.feedright.dto.FarmCreateRequest
import com.purina.feedright.dto.FarmDTO
import com.purina.feedright.service.FarmService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/farms")
class FarmController(private val farmService: FarmService) {

    @GetMapping
    fun getAllFarms(): ResponseEntity<List<FarmDTO>> {
        return ResponseEntity.ok(farmService.getAllFarms())
    }

    @GetMapping("/{id}")
    fun getFarmById(@PathVariable id: UUID): ResponseEntity<FarmDTO> {
        val farm = farmService.getFarmById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(farm)
    }

    @GetMapping("/territory/{territory}")
    fun getFarmsByTerritory(@PathVariable territory: String): ResponseEntity<List<FarmDTO>> {
        return ResponseEntity.ok(farmService.getFarmsByTerritory(territory))
    }

    @PostMapping
    fun createFarm(@RequestBody request: FarmCreateRequest): ResponseEntity<FarmDTO> {
        return ResponseEntity.ok(farmService.createFarm(request))
    }
}
