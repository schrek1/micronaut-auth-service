package cz.schrek.sherdog.controller.mapper

import cz.schrek.sherdog.controller.dto.UserPermissionsResponse
import cz.schrek.sherdog.model.UserPermissions
import org.mapstruct.Mapper

@Mapper
interface UserPermissionsMapper {

    fun mapToUserPermissionsResponse(userPermission: UserPermissions): UserPermissionsResponse

}
