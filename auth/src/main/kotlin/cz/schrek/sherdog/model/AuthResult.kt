package cz.schrek.sherdog.model

import cz.schrek.sherdog.enums.AuthStatus
import java.time.OffsetDateTime

data class AuthResult(
    val status: AuthStatus,
    val token: String? = null,
    val expiration: OffsetDateTime? = null
)
