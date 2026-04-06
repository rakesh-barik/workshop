package com.purina.feedright.service

import com.purina.feedright.dto.*
import com.purina.feedright.model.Visit
import com.purina.feedright.repository.*
import com.purina.feedright.sse.SseEmitterRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class VisitService(
    private val visitRepository: VisitRepository,
    private val salesmanRepository: SalesmanRepository,
    private val farmRepository: FarmRepository,
    private val productRepository: ProductRepository,
    private val sseRegistry: SseEmitterRegistry
) {

    fun getAllVisits(): List<VisitDTO> {
        return visitRepository.findRecentVisits()
            .map { it.toDTO() }
    }

    fun getVisitsBySalesman(salesmanId: UUID): List<VisitDTO> {
        return visitRepository.findBySalesmanIdOrderByVisitDateDesc(salesmanId)
            .map { it.toDTO() }
    }

    fun getVisitsByDateRange(start: LocalDateTime, end: LocalDateTime): List<VisitDTO> {
        return visitRepository.findByVisitDateBetweenOrderByVisitDateDesc(start, end)
            .map { it.toDTO() }
    }

    fun createVisit(request: VisitCreateRequest): VisitDTO {
        // Check for duplicate using deviceId + createdAt (idempotency)
        val existing = visitRepository.findByDeviceIdAndCreatedAt(request.deviceId, request.createdAt)
        if (existing != null) {
            return existing.toDTO()
        }

        val salesman = salesmanRepository.findById(request.salesmanId)
            .orElseThrow { IllegalArgumentException("Salesman not found: ${request.salesmanId}") }

        val farm = farmRepository.findById(request.farmId)
            .orElseThrow { IllegalArgumentException("Farm not found: ${request.farmId}") }

        val product = productRepository.findById(request.productId)
            .orElseThrow { IllegalArgumentException("Product not found: ${request.productId}") }

        val visit = Visit(
            salesman = salesman,
            farm = farm,
            product = product,
            quantity = request.quantity,
            visitDate = request.visitDate,
            notes = request.notes,
            deviceId = request.deviceId,
            createdAt = request.createdAt,
            syncedAt = LocalDateTime.now()
        )

        val dto = visitRepository.save(visit).toDTO()
        sseRegistry.broadcast("visit-created", dto)
        return dto
    }

    fun syncVisits(syncRequest: SyncRequest): SyncResponse {
        var synced = 0
        var failed = 0
        val errors = mutableListOf<String>()
        val syncedVisits = mutableListOf<VisitDTO>()

        syncRequest.visits.forEach { visitRequest ->
            try {
                val dto = createVisit(visitRequest)
                syncedVisits.add(dto)
                synced++
            } catch (e: Exception) {
                failed++
                errors.add("Failed to sync visit: ${e.message}")
            }
        }

        if (syncedVisits.isNotEmpty()) {
            sseRegistry.broadcast("visits-synced", syncedVisits)
        }

        return SyncResponse(synced = synced, failed = failed, errors = errors)
    }

    private fun Visit.toDTO() = VisitDTO(
        id = this.id!!,
        salesmanId = this.salesman.id!!,
        salesmanName = this.salesman.name,
        farmId = this.farm.id!!,
        farmName = this.farm.name,
        productId = this.product.id!!,
        productName = this.product.name,
        productSku = this.product.sku,
        quantity = this.quantity,
        visitDate = this.visitDate,
        notes = this.notes,
        createdAt = this.createdAt,
        syncedAt = this.syncedAt
    )
}
