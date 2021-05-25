package cz.schrek.sherdog.repository

import cz.schrek.sherdog.repository.entity.SherdogUser
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface UserRepository : CrudRepository<SherdogUser, Long> {

    fun findByUsername(username: String): SherdogUser?

    fun findByUserId(userId: UUID): SherdogUser?

}
