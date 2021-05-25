package cz.schrek.sherdog.repository.entity

import cz.schrek.sherdog.enums.UserGroup
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@SequenceGenerator(name = "sherdog_user_id_seq", sequenceName = "sherdog_user_id_seq", allocationSize = 1)
@Table(name = "sherdog_user")
data class SherdogUser(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sherdog_user_id_seq")
    @Column(name = "id", nullable = false)
    val id: Long,

    @NotNull
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: UUID,

    @NotNull
    @Column(name = "username", nullable = false, unique = true)
    val username: String,

    @NotNull
    @Column(name = "password", nullable = false)
    val password: String,

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "group", nullable = false)
    val group: UserGroup
)
