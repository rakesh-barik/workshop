package com.purina.feedright.service

import com.purina.feedright.dto.FarmCreateRequest
import com.purina.feedright.dto.FarmDTO
import com.purina.feedright.model.Farm
import com.purina.feedright.repository.FarmRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class FarmService(private val farmRepository: FarmRepository) {

    fun getAllFarms(): List<FarmDTO> {
        return farmRepository.findAll().map { it.toDTO() }
    }

    fun getFarmsByTerritory(territory: String): List<FarmDTO> {
        return farmRepository.findByTerritory(territory).map { it.toDTO() }
    }

    fun getFarmById(id: UUID): FarmDTO? {
        return farmRepository.findById(id).map { it.toDTO() }.orElse(null)
    }

    fun createFarm(request: FarmCreateRequest): FarmDTO {
        val farm = Farm(
            name = request.name,
            location = request.location,
            territory = request.territory
        )
        return farmRepository.save(farm).toDTO()
    }

    private fun Farm.toDTO() = FarmDTO(
        id = this.id!!,
        name = this.name,
        location = this.location,
        territory = this.territory
    )
}
