package com.purina.feedright.repository

import com.purina.feedright.model.Farm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FarmRepository : JpaRepository<Farm, UUID> {
    fun findByTerritory(territory: String): List<Farm>
    fun findByNameContainingIgnoreCase(name: String): List<Farm>
}
