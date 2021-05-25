package cz.schrek.sherdog.results

import java.time.OffsetDateTime

sealed class AuthResult {
    data class Approved(
        val token: String,
        val expiration: OffsetDateTime
    ) : AuthResult()

    object UserNotFound : AuthResult()
    object PasswordsNotEqual : AuthResult()
}
