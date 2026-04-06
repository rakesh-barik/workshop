package com.purina.feedright.controller

import com.purina.feedright.dto.*
import com.purina.feedright.service.VisitService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/visits")
class VisitController(private val visitService: VisitService) {

    @GetMapping
    fun getAllVisits(): ResponseEntity<List<VisitDTO>> {
        return ResponseEntity.ok(visitService.getAllVisits())
    }

    @GetMapping("/salesman/{salesmanId}")
    fun getVisitsBySalesman(@PathVariable salesmanId: UUID): ResponseEntity<List<VisitDTO>> {
        return ResponseEntity.ok(visitService.getVisitsBySalesman(salesmanId))
    }

    @GetMapping("/range")
    fun getVisitsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) end: LocalDateTime
    ): ResponseEntity<List<VisitDTO>> {
        return ResponseEntity.ok(visitService.getVisitsByDateRange(start, end))
    }

    @PostMapping
    fun createVisit(@RequestBody request: VisitCreateRequest): ResponseEntity<VisitDTO> {
        return ResponseEntity.ok(visitService.createVisit(request))
    }

    @PostMapping("/sync")
    fun syncVisits(@RequestBody syncRequest: SyncRequest): ResponseEntity<SyncResponse> {
        return ResponseEntity.ok(visitService.syncVisits(syncRequest))
    }
}
