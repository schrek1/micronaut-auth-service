package cz.schrek.sherdog.controller

import cz.schrek.sherdog.controller.dto.AuthCheckRequest
import cz.schrek.sherdog.controller.dto.AuthCheckResponse
import cz.schrek.sherdog.controller.dto.AuthRequest
import cz.schrek.sherdog.controller.dto.AuthResponse
import cz.schrek.sherdog.controller.dto.AuthStatus
import cz.schrek.sherdog.results.AuthCheckResult
import cz.schrek.sherdog.results.AuthResult
import cz.schrek.sherdog.service.SecurityService
import cz.schrek.sherdog.service.UserAuthService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Controller("/authentication")
@Validated
@Tag(name = "Authentication")
class AuthController(
    private val userAuthService: UserAuthService,
    private val securityService: SecurityService,
) {

    @Post
    @ApiResponses(ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = AuthResponse::class))]))
    suspend fun authenticate(@Valid @Body request: AuthRequest): HttpResponse<AuthResponse> {
        val authResult = userAuthService.authenticateUser(request.username, request.password)

        val response = when (authResult) {
            is AuthResult.Approved -> AuthResponse(AuthStatus.APPROVED, authResult.token, authResult.expiration)
            else -> AuthResponse(AuthStatus.REJECTED)
        }

        return HttpResponse.ok(response)
    }

    @Post("/check")
    @ApiResponses(ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = AuthCheckResponse::class))]))
    suspend fun checkAuthentication(
        @Valid @Body request: AuthCheckRequest,
        @NotNull @Header("api-key") apiKey: String
    ): HttpResponse<AuthCheckResponse> {
        if (!securityService.isApiKeyAllowed(apiKey)) return HttpResponse.status(HttpStatus.UNAUTHORIZED)

        val authCheckResult = userAuthService.checkAuthentication(request.userId, request.token)

        val response = when (authCheckResult) {
            is AuthCheckResult.Approved -> AuthCheckResponse(AuthStatus.APPROVED, authCheckResult.expiration)
            else -> AuthCheckResponse(AuthStatus.REJECTED)
        }

        return HttpResponse.ok(response)
    }

}
