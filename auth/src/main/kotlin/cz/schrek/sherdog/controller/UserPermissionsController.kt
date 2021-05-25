package cz.schrek.sherdog.controller

import cz.schrek.sherdog.controller.dto.UserPermissionsResponse
import cz.schrek.sherdog.results.UserPermissionsResult
import cz.schrek.sherdog.service.SecurityService
import cz.schrek.sherdog.service.UserPermissionsService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import javax.validation.constraints.NotNull

@Controller("/user/{userId}/permissions")
@Validated
@Tag(name = "User permissions")
class UserPermissionsController(
    private val userPermissionsService: UserPermissionsService,
    private val securityService: SecurityService,
) {

    @Get
    suspend fun getPermissionsForUser(
        @NotNull @PathVariable("userId") userId: UUID,
        @NotNull @Header("api-key") apiKey: String
    ): HttpResponse<UserPermissionsResponse> {
        if (!securityService.isApiKeyAllowed(apiKey)) return HttpResponse.status(HttpStatus.UNAUTHORIZED)

        val userPermissions = userPermissionsService.getUserPermissions(userId)

        val response = when (userPermissions) {
            is UserPermissionsResult.Ok -> UserPermissionsResponse(userPermissions.groups)
            else -> UserPermissionsResponse(emptyList())
        }

        return HttpResponse.ok(response)
    }
}
