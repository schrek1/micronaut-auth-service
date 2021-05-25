package cz.schrek.sherdog.controller.dto

import cz.schrek.sherdog.enums.UserGroup
import javax.validation.constraints.NotNull

data class UserPermissionsResponse(
    @field:NotNull val groups: List<UserGroup>
)
