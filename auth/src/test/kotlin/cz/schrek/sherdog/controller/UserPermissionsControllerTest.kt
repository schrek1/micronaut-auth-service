package cz.schrek.sherdog.controller

import cz.schrek.sherdog.controller.dto.AuthCheckResponse
import cz.schrek.sherdog.controller.dto.UserPermissionsResponse
import cz.schrek.sherdog.enums.UserGroup
import cz.schrek.sherdog.model.UserPermissions
import cz.schrek.sherdog.service.SecurityService
import cz.schrek.sherdog.service.UserPermissionsService
import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@MicronautTest
internal class UserPermissionsControllerTest(
    private val userPermissionsService: UserPermissionsService,
    private val securityService: SecurityService
) {

    @field:Inject
    @field:Client(id = "/")
    lateinit var httpClient: RxHttpClient


    @Test
    fun getPermissionsForUser_validRequest_shouldPassed() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"
        val USER_ID = UUID.fromString("4f38c061-150a-4770-bb7c-e80609f5e5a2")

        Mockito.`when`(securityService.isApiKeyAllowed(API_KEY)).then { true }

        Mockito.`when`(userPermissionsService.getUserPermissions(USER_ID)).then {
            UserPermissions(listOf(UserGroup.ADMIN))
        }

        val request: MutableHttpRequest<Unit> = HttpRequest.GET("/user/${USER_ID}/permissions")
        request.header("api-key", API_KEY)

        val response = httpClient.toBlocking().exchange<Unit, UserPermissionsResponse>(
            request, UserPermissionsResponse::class.java
        )

        Assertions.assertThat(response).isNotNull
        Assertions.assertThat(response.status.code).isEqualTo(200)

        val body = response.body()
        Assertions.assertThat(body).isNotNull
        Assertions.assertThat(body?.groups).containsExactlyInAnyOrder(UserGroup.ADMIN)
    }


    @Test
    fun checkAuthentication_invalidApiKey_shouldReturnError() {
        val API_KEY = "9295df17-871d-423b-b755-8ac097704b00"
        val USER_ID = UUID.fromString("73e41921-8c61-41c8-8326-9e8b99fcdf78")

        Mockito.`when`(securityService.isApiKeyAllowed(API_KEY)).then { false }

        var isThrown = false

        try {
            val request: MutableHttpRequest<Unit> = HttpRequest.GET("/user/${USER_ID}/permissions")
            request.header("api-key", API_KEY)


            httpClient.toBlocking().retrieve(
                request, AuthCheckResponse::class.java
            )
        } catch (ex: HttpClientResponseException) {
            isThrown = true

            val httpResponse = ex.response
            val body = httpResponse.getBody(String::class.java)

            Assertions.assertThat(httpResponse).isNotNull
            Assertions.assertThat(httpResponse.status().code).isEqualTo(401)
        }

        Assertions.assertThat(isThrown).`as`("exception not thrown").isTrue
    }

    @MockBean(UserPermissionsService::class)
    fun mockUserPermissionsService(): UserPermissionsService = Mockito.mock(UserPermissionsService::class.java)

    @MockBean(SecurityService::class)
    fun mockSecurityService(): SecurityService = Mockito.mock(SecurityService::class.java)
}

