package ai.lail.api.permissions.nodes

import ai.lail.api.util.Reflect

/**
 * Permission nodes for encounters.
 */
object EncounterPermission {
    const val ROOT = "encounters"                        // Allows access to all nodes
    const val VIEW = "$ROOT.view"                        // Allows viewing encounters
    const val VIEW_FILTER = "$VIEW.filter"               // Allows filtering encounters with restrictions
    const val SELF = "$ROOT.self"                        // Allows all actions on your own encounters
    const val SELF_VIEW = "$SELF.view"                   // Allows viewing your own encounters
    const val SELF_CREATE = "$SELF.create"               // Allows creating encounters for yourself
    const val SELF_EDIT = "$SELF.edit"                   // Allows editing your own encounters
    const val SELF_DELETE = "$SELF.delete"               // Allows deleting your own encounters

    // Administrative permissions
    const val MANAGE = "$ROOT.manage"                    // Allows managing encounters
    const val MANAGE_VIEW = "$MANAGE.view"               // Allows viewing encounters (regardless of privacy/ownership)
    const val MANAGE_VIEW_FILTER = "$MANAGE_VIEW.filter" // Allows filtering encounters without restrictions
    const val MANAGE_CREATE = "$MANAGE.create"           // Allows creating encounters (also in others' names)
    const val MANAGE_EDIT = "$MANAGE.edit"               // Allows editing encounters
    const val MANAGE_DELETE = "$MANAGE.delete"           // Allows deleting encounters

    val defaults = listOf(VIEW, SELF)

    fun getAllPermissions(): Set<String> {
        return Reflect.getAllConstValues(this::class)
    }

    fun isValidPermission(permission: String): Boolean {
        return getAllPermissions().contains(permission)
    }

    fun areValidPermissions(permissions: List<String>): Boolean {
        return getAllPermissions().containsAll(permissions)
    }
}