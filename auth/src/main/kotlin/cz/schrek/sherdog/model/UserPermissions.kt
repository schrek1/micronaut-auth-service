package cz.schrek.sherdog.model

import cz.schrek.sherdog.enums.UserGroup

data class UserPermissions(
    val groups:List<UserGroup> = emptyList()
)
