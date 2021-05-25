package cz.schrek.sherdog.service

import cz.schrek.sherdog.enums.UserGroup
import cz.schrek.sherdog.model.AuthCheckResult
import cz.schrek.sherdog.model.AuthResult
import cz.schrek.sherdog.model.UserPermissions
import java.util.*

interface UserAuthService {

    fun authenticateUser(username: String, password: String): AuthResult

    fun checkAuthentication(userId: UUID, token: String): AuthCheckResult

}
