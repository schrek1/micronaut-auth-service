package cz.schrek.sherdog.repository.entity

import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@SequenceGenerator(name = "auth_token_id_seq", sequenceName = "auth_token_id_seq", allocationSize = 1)
@Table(name = "auth_token")
data class AuthToken(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_token_id_seq")
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @NotNull
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: UUID,

    @NotNull
    @Column(name = "token", nullable = false)
    val token: String,

    @NotNull
    @Column(name = "expiration", nullable = false)
    val expiration: OffsetDateTime
)
