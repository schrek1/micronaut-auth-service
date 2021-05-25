package cz.schrek.sherdog.service.impl

import cz.schrek.sherdog.config.logster
import cz.schrek.sherdog.repository.UserRepository
import cz.schrek.sherdog.results.UserPermissionsResult
import cz.schrek.sherdog.service.UserPermissionsService
import java.util.*
import javax.inject.Singleton

@Singleton
class UserPermissionsServiceImpl(
    private val userRepository: UserRepository
) : UserPermissionsService {

    private val log by logster()

    override fun getUserPermissions(userId: UUID): UserPermissionsResult {
        log.info("obtaining user (id=$userId) permission started")

        val user = userRepository.findByUserId(userId)

        return when {
            user != null -> {
                log.debug("user (id=$userId) belongs to groups [${user.group}]")
                UserPermissionsResult.Ok(listOf(user.group))
            }
            else -> {
                log.debug("user (id=$userId) doesn't belong to any group")
                UserPermissionsResult.NotBelongsToAnyGroup
            }
        }
    }
}
