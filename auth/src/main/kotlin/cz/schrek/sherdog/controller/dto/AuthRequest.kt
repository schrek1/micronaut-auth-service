package cz.schrek.sherdog.controller.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class AuthRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)
