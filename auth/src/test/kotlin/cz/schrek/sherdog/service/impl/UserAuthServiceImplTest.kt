package cz.schrek.sherdog.service.impl

import cz.schrek.sherdog.enums.AuthStatus
import cz.schrek.sherdog.enums.UserGroup
import cz.schrek.sherdog.repository.AuthTokenRepository
import cz.schrek.sherdog.repository.UserRepository
import cz.schrek.sherdog.repository.entity.AuthToken
import cz.schrek.sherdog.repository.entity.SherdogUser
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import java.time.OffsetDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest
internal class UserAuthServiceImplTest {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var authTokenRepository: AuthTokenRepository

    @Inject
    lateinit var userAuthServiceImpl: UserAuthServiceImpl

    @Test
    fun authenticateUser_allValid_shouldAuthorized() {
        val USER_ID = UUID.fromString("048d80a2-6a2a-461d-8c8e-fe25728ef52a")

        `when`(userRepository.findByUsername("schrek1")).then {
            SherdogUser(
                id = 1,
                userId = USER_ID,
                username = "schrek1",
                password = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4",
                group = UserGroup.ADMIN
            )
        }

        val (status, token, expiration) = userAuthServiceImpl.authenticateUser("schrek1", "1234")

        assertThat(status).isEqualTo(AuthStatus.APPROVED)
        assertThat(token).isNotNull
        assertThat(expiration).isBetween(
            OffsetDateTime.now().plusHours(4).plusMinutes(58),
            OffsetDateTime.now().plusHours(5).plusMinutes(2),
        )

        verify(authTokenRepository, times(1)).deleteAllByUserId(USER_ID)

        val savedAuthTokenCaptor = ArgumentCaptor.forClass(AuthToken::class.java)
        verify(authTokenRepository, times(1)).save(savedAuthTokenCaptor.capture())

        val (id, userId, savedToken, savedExpiration) = savedAuthTokenCaptor.value
        assertThat(id).isEqualTo(0)
        assertThat(userId).isEqualTo(USER_ID)
        assertThat(savedToken).isNotNull.isEqualTo(token)
        assertThat(savedExpiration).isEqualTo(expiration)
    }

    @Test
    fun authenticateUser_userNotFound_shouldRejected() {
        `when`(userRepository.findByUsername("schrek1")).then {
            null
        }

        val (status, token, expiration) = userAuthServiceImpl.authenticateUser("schrek1", "1234")

        assertThat(status).isEqualTo(AuthStatus.REJECTED)
        assertThat(token).isNull()
        assertThat(expiration).isNull()

        verifyNoInteractions(authTokenRepository)
    }

    @Test
    fun authenticateUser_passwordNotMatch_shouldRejected() {
        val USER_ID = UUID.fromString("048d80a2-6a2a-461d-8c8e-fe25728ef52a")

        `when`(userRepository.findByUsername("schrek1")).then {
            SherdogUser(
                id = 1,
                userId = USER_ID,
                username = "schrek1",
                password = "adsdasdafzxc",
                group = UserGroup.ADMIN
            )
        }

        val (status, token, expiration) = userAuthServiceImpl.authenticateUser("schrek1", "1234")

        assertThat(status).isEqualTo(AuthStatus.REJECTED)
        assertThat(token).isNull()
        assertThat(expiration).isNull()

        verifyNoInteractions(authTokenRepository)
    }

    @Test
    fun checkAuthentication_authIsValid_shouldReturnApproved() {
        val USER_ID = UUID.fromString("240e7f0c-628b-4b16-81d5-f82bcc2e01fc")
        val TOKEN = "tTokenValue"
        val EXPIRATION = OffsetDateTime.now().plusHours(5)

        `when`(authTokenRepository.findByUserId(USER_ID)).then {
            AuthToken(
                id = 1,
                userId = USER_ID,
                token = TOKEN,
                expiration = EXPIRATION
            )
        }

        val (authStatus, expiration) = userAuthServiceImpl.checkAuthentication(USER_ID, TOKEN)

        assertThat(authStatus).isEqualTo(AuthStatus.APPROVED)
        assertThat(expiration).isEqualTo(EXPIRATION)
    }

    @Test
    fun checkAuthentication_authTokenNotFound_shouldReturnRejected() {
        val USER_ID = UUID.fromString("240e7f0c-628b-4b16-81d5-f82bcc2e01fc")
        val TOKEN = "tTokenValue"

        `when`(authTokenRepository.findByUserId(USER_ID)).then {
            null
        }

        val (authStatus, expiration) = userAuthServiceImpl.checkAuthentication(USER_ID, TOKEN)

        assertThat(authStatus).isEqualTo(AuthStatus.REJECTED)
        assertThat(expiration).isNull()
    }

    @Test
    fun checkAuthentication_authTokenIsExpired_shouldReturnRejected() {
        val USER_ID = UUID.fromString("240e7f0c-628b-4b16-81d5-f82bcc2e01fc")
        val TOKEN = "tTokenValue"
        val EXPIRATION = OffsetDateTime.now().minusMinutes(1)

        `when`(authTokenRepository.findByUserId(USER_ID)).then {
            AuthToken(
                id = 1,
                userId = USER_ID,
                token = TOKEN,
                expiration = EXPIRATION
            )
        }

        val (authStatus, expiration) = userAuthServiceImpl.checkAuthentication(USER_ID, TOKEN)

        assertThat(authStatus).isEqualTo(AuthStatus.REJECTED)
        assertThat(expiration).isNull()

        val removedAuthTokenCaptor = ArgumentCaptor.forClass(AuthToken::class.java)
        verify(authTokenRepository, times(1)).delete(removedAuthTokenCaptor.capture())
        assertThat(removedAuthTokenCaptor.value).isNotNull
        assertThat(removedAuthTokenCaptor.value.id).isEqualTo(1)
        assertThat(removedAuthTokenCaptor.value.userId).isEqualTo(USER_ID)
        assertThat(removedAuthTokenCaptor.value.token).isEqualTo(TOKEN)
        assertThat(removedAuthTokenCaptor.value.expiration).isEqualTo(EXPIRATION)
    }

    @Test
    fun checkAuthentication_authTokenNotMatched_shouldReturnRejected() {
        val USER_ID = UUID.fromString("240e7f0c-628b-4b16-81d5-f82bcc2e01fc")
        val TOKEN = "tTokenValue"
        val EXPIRATION = OffsetDateTime.now().minusMinutes(1)

        `when`(authTokenRepository.findByUserId(USER_ID)).then {
            AuthToken(
                id = 1,
                userId = USER_ID,
                token = "fakjhgsadkf",
                expiration = EXPIRATION
            )
        }

        val (authStatus, expiration) = userAuthServiceImpl.checkAuthentication(USER_ID, TOKEN)

        assertThat(authStatus).isEqualTo(AuthStatus.REJECTED)
        assertThat(expiration).isNull()

        val removedAuthTokenCaptor = ArgumentCaptor.forClass(AuthToken::class.java)
        verify(authTokenRepository, times(1)).delete(removedAuthTokenCaptor.capture())
        assertThat(removedAuthTokenCaptor.value).isNotNull
        assertThat(removedAuthTokenCaptor.value.id).isEqualTo(1)
        assertThat(removedAuthTokenCaptor.value.userId).isEqualTo(USER_ID)
        assertThat(removedAuthTokenCaptor.value.token).isEqualTo("fakjhgsadkf")
        assertThat(removedAuthTokenCaptor.value.expiration).isEqualTo(EXPIRATION)
    }


    @MockBean(UserRepository::class)
    fun mockUserRepository(): UserRepository = mock(UserRepository::class.java)

    @MockBean(AuthTokenRepository::class)
    fun mockAuthTokenRepository(): AuthTokenRepository = mock(AuthTokenRepository::class.java)
}
