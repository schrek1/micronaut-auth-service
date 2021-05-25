package cz.schrek.sherdog.controller

import cz.schrek.sherdog.controller.dto.AuthCheckRequest
import cz.schrek.sherdog.controller.dto.AuthCheckResponse
import cz.schrek.sherdog.controller.dto.AuthRequest
import cz.schrek.sherdog.controller.dto.AuthResponse
import cz.schrek.sherdog.enums.AuthStatus
import cz.schrek.sherdog.model.AuthCheckResult
import cz.schrek.sherdog.model.AuthResult
import cz.schrek.sherdog.service.SecurityService
import cz.schrek.sherdog.service.UserAuthService
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.OffsetDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest
internal class AuthControllerTest(
    val userAuthService: UserAuthService,
    val securityService: SecurityService
) {

    @field:Inject
    @field:Client(id = "/")
    lateinit var httpClient: RxHttpClient


    @Test
    fun authenticate_validRequest_shouldPassed() {
        val TOKEN = "tTokenValue"
        val EXPIRATION = OffsetDateTime.now()

        `when`(userAuthService.authenticateUser("schrek1", "heslo")).then {
            AuthResult(AuthStatus.APPROVED, TOKEN, EXPIRATION)
        }

        val response = httpClient.toBlocking().exchange(
            HttpRequest.POST("/authentication", AuthRequest("schrek1", "heslo")),
            AuthResponse::class.java
        )

        assertThat(response).isNotNull
        assertThat(response.status.code).isEqualTo(200)

        val body = response.body()
        assertThat(body).isNotNull
        assertThat(body?.status).isEqualTo(AuthStatus.APPROVED)
        assertThat(body?.token).isEqualTo(TOKEN)
        assertThat(body?.expiration).isEqualTo(EXPIRATION)
    }

    @Test
    fun authenticate_blankUsername_shouldReturnError() {
        val REQUEST_BODY = """
                    {
                    "username":"",
                    "password":"heslo"
                    }
                """.trimIndent()

        var isThrown = false

        try {
            httpClient.toBlocking().exchange(
                HttpRequest.POST("/authentication", REQUEST_BODY), String::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
            assertThat(body).isPresent
            assertThat(body.get()).contains("p0.username: nesm\u00ED b\u00FDt pr\u00E1zdn\u00E1")
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }

    @Test
    fun authenticate_missingUsername_shouldReturnError() {
        val REQUEST_BODY = """
                    {
                    "password":"heslo"
                    }
                """.trimIndent()

        var isThrown = false

        try {
            httpClient.toBlocking().exchange(
                HttpRequest.POST("/authentication", REQUEST_BODY), String::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
            assertThat(body).isPresent
            assertThat(body.get()).contains("Missing required creator property 'username'")
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }


    @Test
    fun authenticate_blankPassword_shouldReturnError() {
        val REQUEST_BODY = """
                    {
                    "username":"schrek1",
                    "password":""
                    }
                """.trimIndent()

        var isThrown = false

        try {
            httpClient.toBlocking().exchange(
                HttpRequest.POST("/authentication", REQUEST_BODY), String::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
            assertThat(body).isPresent
            assertThat(body.get()).contains("p0.password: nesm\u00ED b\u00FDt pr\u00E1zdn\u00E1")
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }

    @Test
    fun authenticate_missingPassword_shouldReturnError() {
        val REQUEST_BODY = """
                    {
                    "username":"schrek1"
                    }
                """.trimIndent()


        var isThrown = false

        try {
            httpClient.toBlocking().exchange(
                HttpRequest.POST("/authentication", REQUEST_BODY), String::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
            assertThat(body).isPresent
            assertThat(body.get()).contains("Missing required creator property 'password'")
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }


    @Test
    fun checkAuthentication_allValid_shouldReturn() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"
        val USER_ID = UUID.fromString("73e41921-8c61-41c8-8326-9e8b99fcdf78")
        val TOKEN = "tTokenValue"
        val EXPIRATION = OffsetDateTime.now()

        `when`(securityService.isApiKeyAllowed(API_KEY)).then { true }

        `when`(userAuthService.checkAuthentication(USER_ID, TOKEN)).then {
            AuthCheckResult(AuthStatus.APPROVED, EXPIRATION)
        }

        val request = HttpRequest
            .POST("/authentication/check", AuthCheckRequest(USER_ID, TOKEN))
            .header("api-key", API_KEY)

        val response = httpClient.toBlocking().exchange(
            request, AuthCheckResponse::class.java
        )

        assertThat(response).isNotNull
        assertThat(response.status.code).isEqualTo(200)

        val body = response.body()
        assertThat(body).isNotNull
        assertThat(body?.authStatus).isEqualTo(AuthStatus.APPROVED)
        assertThat(body?.expiration).isEqualTo(EXPIRATION)
    }

    @Test
    fun checkAuthentication_invalidApiKey_shouldReturnError() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"
        val USER_ID = UUID.fromString("73e41921-8c61-41c8-8326-9e8b99fcdf78")
        val TOKEN = "tTokenValue"

        `when`(securityService.isApiKeyAllowed(API_KEY)).then { false }

        var isThrown = false

        try {
            val request = HttpRequest
                .POST("/authentication/check", AuthCheckRequest(USER_ID, TOKEN))
                .header("api-key", API_KEY)

            httpClient.toBlocking().retrieve(
                request, AuthCheckResponse::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(401)
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }

    @Test
    fun checkAuthentication_missingApiKey_shouldReturnError() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"
        val USER_ID = UUID.fromString("73e41921-8c61-41c8-8326-9e8b99fcdf78")
        val TOKEN = "tTokenValue"

        `when`(securityService.isApiKeyAllowed(API_KEY)).then { false }

        var isThrown = false

        try {
            val request = HttpRequest
                .POST("/authentication/check", AuthCheckRequest(USER_ID, TOKEN))

            httpClient.toBlocking().retrieve(
                request, AuthCheckResponse::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
            assertThat(body).isPresent
            assertThat(body.get()).contains("Required Header [api-key] not specified")
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }

    @Test
    fun checkAuthentication_missingUserId_shouldReturnError() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"

        val REQUEST_BODY = """
            {
            "token":"tTokenValue"
            }
        """.trimIndent()

        var isThrown = false

        try {
            val request = HttpRequest
                .POST("/authentication/check", REQUEST_BODY)
                .header("api-key", API_KEY)

            httpClient.toBlocking().retrieve(
                request, AuthCheckResponse::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
            assertThat(body).isPresent
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }

    @Test
    fun checkAuthentication_emptyUserId_shouldReturnError() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"

        val REQUEST_BODY = """
            {
            "userId":"",
            "token":"tTokenValue"
            }
        """.trimIndent()

        var isThrown = false

        try {
            val request = HttpRequest
                .POST("/authentication/check", REQUEST_BODY)
                .header("api-key", API_KEY)

            httpClient.toBlocking().retrieve(
                request, AuthCheckResponse::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }

    @Test
    fun checkAuthentication_missingToken_shouldReturnError() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"

        val REQUEST_BODY = """
            {
            "userId":"73e41921-8c61-41c8-8326-9e8b99fcdf78"
            }
        """.trimIndent()

        var isThrown = false

        try {
            val request = HttpRequest
                .POST("/authentication/check", REQUEST_BODY)
                .header("api-key", API_KEY)

            httpClient.toBlocking().retrieve(
                request, AuthCheckResponse::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
            assertThat(body).isPresent
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }

    @Test
    fun checkAuthentication_emptyToken_shouldReturnError() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"

        val REQUEST_BODY = """
            {
            "userId":"73e41921-8c61-41c8-8326-9e8b99fcdf78",
            "token":""
            }
        """.trimIndent()

        var isThrown = false

        try {
            val request = HttpRequest
                .POST("/authentication/check", REQUEST_BODY)
                .header("api-key", API_KEY)

            httpClient.toBlocking().retrieve(
                request, AuthCheckResponse::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            assertThat(httpResponse).isNotNull
            assertThat(httpResponse.status().code).isEqualTo(400)
            assertThat(body).isPresent
            assertThat(body.get()).contains("p0.token: nesm\u00ED b\u00FDt pr\u00E1zdn\u00E1")
        }

        assertThat(isThrown).`as`("exception not thrown").isTrue()
    }

    @MockBean(UserAuthService::class)
    fun mockUserAuthService(): UserAuthService = mock(UserAuthService::class.java)

    @MockBean(SecurityService::class)
    fun mockSecurityService(): SecurityService = mock(SecurityService::class.java)
}
