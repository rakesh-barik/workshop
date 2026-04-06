package com.purina.feedright.controller

import com.purina.feedright.dto.AuthRequest
import com.purina.feedright.dto.AuthResponse
import com.purina.feedright.service.SalesmanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val salesmanService: SalesmanService) {

    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = salesmanService.authenticate(authRequest)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).build()
        }
    }
}
