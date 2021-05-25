package cz.schrek.sherdog.service.impl

import cz.schrek.sherdog.config.logster
import cz.schrek.sherdog.repository.AuthTokenRepository
import cz.schrek.sherdog.repository.UserRepository
import cz.schrek.sherdog.repository.entity.AuthToken
import cz.schrek.sherdog.results.AuthCheckResult
import cz.schrek.sherdog.results.AuthResult
import cz.schrek.sherdog.service.UserAuthService
import cz.schrek.sherdog.util.toHexString
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.util.*
import javax.inject.Singleton

@Singleton
class UserAuthServiceImpl(
    private val userRepository: UserRepository,
    private val authTokenRepository: AuthTokenRepository
) : UserAuthService {

    companion object {
        private val log by logster()
        private val hasher = MessageDigest.getInstance("SHA-256")
    }

    override fun authenticateUser(username: String, password: String): AuthResult {
        log.info("user ($username) authentication started")

        val user = userRepository.findByUsername(username)

        if (user == null) {
            log.info("User ($username) not found - rejected")
            return AuthResult.UserNotFound
        }

        val hashedPassword = hasher.digest(password.toByteArray())?.toHexString()

        if (user.password != hashedPassword) {
            log.info("user ($username) authentication was rejected - entered password not correct")
            return AuthResult.PasswordsNotEqual
        }

        authTokenRepository.deleteAllByUserId(user.userId)

        val newToken = AuthToken(
            userId = user.userId,
            token = UUID.randomUUID().toString(),
            expiration = OffsetDateTime.now().plusHours(5)
        )

        authTokenRepository.save(newToken)

        log.info("user ($username) authentication was successful")

        return AuthResult.Approved(newToken.token, newToken.expiration)
    }

    override fun checkAuthentication(userId: UUID, token: String): AuthCheckResult {
        log.info("checking user (id=$userId) authentication started")

        val authToken = authTokenRepository.findByUserId(userId)

        if (authToken == null) {
            log.info("token for user (id=$userId) not found - rejected")
            return AuthCheckResult.TokenNotFound
        }

        val isValid = authToken.expiration.isAfter(OffsetDateTime.now()) && authToken.token == token

        if (isValid) {
            log.info("token for user (id=$userId) is found and valid - approved")
            return AuthCheckResult.Approved(authToken.expiration)
        }

        log.info("authToken (id=${authToken.id}) is expired, user (id=$userId) - rejected")

        authTokenRepository.delete(authToken)
        log.debug("authToken (id=${authToken.id}) removed from db...")

        return AuthCheckResult.TokenIsInvalid
    }

}
