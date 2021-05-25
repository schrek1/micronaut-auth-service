package cz.schrek.sherdog.repository

import cz.schrek.sherdog.repository.entity.AuthToken
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface AuthTokenRepository : CrudRepository<AuthToken, Long> {

    fun deleteAllByUserId(userId: UUID)

    fun findByUserId(userId: UUID): AuthToken?

}
