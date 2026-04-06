package com.purina.feedright.repository

import com.purina.feedright.model.Visit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface VisitRepository : JpaRepository<Visit, UUID> {
    // Find by device_id and created_at for idempotency
    fun findByDeviceIdAndCreatedAt(deviceId: String, createdAt: LocalDateTime): Visit?

    // Find visits by salesman
    fun findBySalesmanIdOrderByVisitDateDesc(salesmanId: UUID): List<Visit>

    // Find visits by farm
    fun findByFarmIdOrderByVisitDateDesc(farmId: UUID): List<Visit>

    // Find visits in date range
    fun findByVisitDateBetweenOrderByVisitDateDesc(start: LocalDateTime, end: LocalDateTime): List<Visit>

    // Find visits by salesman in date range
    @Query("SELECT v FROM Visit v WHERE v.salesman.id = :salesmanId AND v.visitDate BETWEEN :start AND :end ORDER BY v.visitDate DESC")
    fun findBySalesmanAndDateRange(
        @Param("salesmanId") salesmanId: UUID,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime
    ): List<Visit>

    // Get recent visits across all salesmen
    @Query("SELECT v FROM Visit v ORDER BY v.visitDate DESC")
    fun findRecentVisits(): List<Visit>
}
