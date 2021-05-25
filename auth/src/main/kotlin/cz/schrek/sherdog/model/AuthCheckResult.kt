package cz.schrek.sherdog.model

import cz.schrek.sherdog.enums.AuthStatus
import java.time.OffsetDateTime

data class AuthCheckResult(
    val authStatus: AuthStatus,
    val expiration: OffsetDateTime? = null
)
