package cz.schrek.sherdog.service

import cz.schrek.sherdog.model.UserPermissions
import java.util.*

interface UserPermissionsService {

    fun getUserPermissions(userId: UUID): UserPermissions

}
