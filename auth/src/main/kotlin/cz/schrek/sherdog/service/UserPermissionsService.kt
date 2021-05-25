package cz.schrek.sherdog.service

import cz.schrek.sherdog.results.UserPermissionsResult
import java.util.*

interface UserPermissionsService {

    fun getUserPermissions(userId: UUID): UserPermissionsResult

}
