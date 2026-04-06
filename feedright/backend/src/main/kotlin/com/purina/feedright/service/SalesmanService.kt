package com.purina.feedright.service

import com.purina.feedright.dto.AuthRequest
import com.purina.feedright.dto.AuthResponse
import com.purina.feedright.dto.SalesmanDTO
import com.purina.feedright.model.Salesman
import com.purina.feedright.repository.SalesmanRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class SalesmanService(
    private val salesmanRepository: SalesmanRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun getAllSalesmen(): List<SalesmanDTO> {
        return salesmanRepository.findAll().map { it.toDTO() }
    }

    fun getSalesmanById(id: UUID): SalesmanDTO? {
        return salesmanRepository.findById(id).map { it.toDTO() }.orElse(null)
    }

    fun getSalesmenByTerritory(territory: String): List<SalesmanDTO> {
        return salesmanRepository.findByTerritory(territory).map { it.toDTO() }
    }

    fun authenticate(authRequest: AuthRequest): AuthResponse {
        val salesman = salesmanRepository.findByPhone(authRequest.phone)
            ?: throw IllegalArgumentException("Invalid phone or PIN")

        // For MVP, we're using simple PIN comparison
        // In production, you'd use passwordEncoder.matches()
        if (!passwordEncoder.matches(authRequest.pin, salesman.pin)) {
            throw IllegalArgumentException("Invalid phone or PIN")
        }

        // For MVP, we'll use a simple token (just the salesman ID)
        // In production, use JWT with proper signing
        val token = "Bearer-${salesman.id}"

        return AuthResponse(
            token = token,
            salesman = salesman.toDTO()
        )
    }

    private fun Salesman.toDTO() = SalesmanDTO(
        id = this.id!!,
        name = this.name,
        phone = this.phone,
        territory = this.territory
    )
}
