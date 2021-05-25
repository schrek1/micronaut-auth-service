package cz.schrek.sherdog.service.impl

import cz.schrek.sherdog.config.logster
import cz.schrek.sherdog.model.UserPermissions
import cz.schrek.sherdog.repository.UserRepository
import cz.schrek.sherdog.service.UserPermissionsService
import java.util.*
import javax.inject.Singleton

@Singleton
class UserPermissionsServiceImpl(
    private val userRepository: UserRepository
) : UserPermissionsService {

    private val log by logster()

    override fun getUserPermissions(userId: UUID): UserPermissions {
        log.info("obtaining user (id=$userId) permission started")

        val user = userRepository.findByUserId(userId)

        val userGroups = when {
            user != null -> {
                log.debug("user (id=$userId) belongs to groups [${user.group}]")
                listOf(user.group)
            }
            else -> {
                log.debug("user (id=$userId) doesn't belong to any group")
                emptyList()
            }
        }

        return UserPermissions(userGroups)
    }
}
