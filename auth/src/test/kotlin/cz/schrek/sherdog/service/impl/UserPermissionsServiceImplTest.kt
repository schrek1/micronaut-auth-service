package cz.schrek.sherdog.service.impl

import cz.schrek.sherdog.enums.UserGroup
import cz.schrek.sherdog.repository.UserRepository
import cz.schrek.sherdog.repository.entity.SherdogUser
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import javax.inject.Inject

@MicronautTest
internal class UserPermissionsServiceImplTest {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userPermissionsServiceImpl: UserPermissionsServiceImpl

    @Test
    fun getUserPermissions_allValid_shouldReturnUserPermissions() {
        val USER_ID = UUID.fromString("1df8c23f-1b98-4fa7-ad86-983ed52c88b8")

        `when`(userRepository.findByUserId(USER_ID)).then {
            SherdogUser(
                id = 1,
                userId = USER_ID,
                username = "schrek1",
                password = "1234",
                group = UserGroup.ADMIN
            )
        }

        val (groups) = userPermissionsServiceImpl.getUserPermissions(USER_ID)

        assertThat(groups).containsExactlyInAnyOrder(UserGroup.ADMIN)
    }

    @Test
    fun getUserPermissions_userPermissionsNotFound_shouldReturnEmptyPermissions() {
        val USER_ID = UUID.fromString("1df8c23f-1b98-4fa7-ad86-983ed52c88b8")

        `when`(userRepository.findByUserId(USER_ID)).then {
            null
        }

        val (groups) = userPermissionsServiceImpl.getUserPermissions(USER_ID)

        assertThat(groups).isEmpty()
    }

    @MockBean(UserRepository::class)
    fun mockUserRepository(): UserRepository = mock(UserRepository::class.java)
}
