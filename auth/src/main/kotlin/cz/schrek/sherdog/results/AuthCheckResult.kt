package cz.schrek.sherdog.results

import java.time.OffsetDateTime

sealed class AuthCheckResult {
    data class Approved(val expiration: OffsetDateTime) : AuthCheckResult()
    object TokenNotFound : AuthCheckResult()
    object TokenIsInvalid : AuthCheckResult()
}
