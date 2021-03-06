package cz.schrek.sherdog.controller.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.Nullable
import java.time.OffsetDateTime
import javax.validation.constraints.NotNull

@Introspected
data class AuthResponse(
    @field:NotNull val status: AuthStatus,
    @field:Nullable val token: String? = null,
    @field:Nullable val expiration: OffsetDateTime? = null
)
