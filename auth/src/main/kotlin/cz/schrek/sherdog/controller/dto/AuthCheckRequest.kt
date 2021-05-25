package cz.schrek.sherdog.controller.dto

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class AuthCheckRequest(
    @field:NotNull val userId: UUID,
    @field:NotBlank val token: String
)
