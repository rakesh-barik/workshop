package com.purina.feedright.repository

import com.purina.feedright.model.Salesman
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SalesmanRepository : JpaRepository<Salesman, UUID> {
    fun findByPhone(phone: String): Salesman?
    fun findByTerritory(territory: String): List<Salesman>
}
