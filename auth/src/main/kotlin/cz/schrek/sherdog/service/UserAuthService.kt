package cz.schrek.sherdog.service

import cz.schrek.sherdog.results.AuthCheckResult
import cz.schrek.sherdog.results.AuthResult
import java.util.*

interface UserAuthService {

    fun authenticateUser(username: String, password: String): AuthResult

    fun checkAuthentication(userId: UUID, token: String): AuthCheckResult

}
