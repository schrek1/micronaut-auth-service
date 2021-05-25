package cz.schrek.sherdog.results

import cz.schrek.sherdog.enums.UserGroup

sealed class UserPermissionsResult {
    data class Ok(val groups: List<UserGroup>) : UserPermissionsResult()
    object NotBelongsToAnyGroup : UserPermissionsResult()
}
