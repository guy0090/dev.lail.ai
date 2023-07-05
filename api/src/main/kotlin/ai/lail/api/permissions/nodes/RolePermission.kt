package ai.lail.api.permissions.nodes

import ai.lail.api.util.Reflect

object RolePermission {
    const val ROOT = "roles"
    const val SELF = "$ROOT.self" // Allows getting your own roles
    const val MANAGE = "$ROOT.manage" // Allows managing role
    const val MANAGE_VIEW = "$MANAGE.view" // Allows viewing roles
    const val MANAGE_ADD = "$MANAGE.add" // Allows adding roles
    const val MANAGE_REMOVE = "$MANAGE.remove" // Allows removing roles
    const val MANAGE_EDIT = "$MANAGE.edit" // Allows editing roles
    const val MANAGE_PERMISSIONS = "$MANAGE.permissions" // Allows managing permissions assigned to roles
    const val MANAGE_PERMISSIONS_VIEW = "$MANAGE_PERMISSIONS.view" // Allows viewing permissions
    const val MANAGE_PERMISSIONS_ADD = "$MANAGE_PERMISSIONS.add" // Allows adding permissions
    const val MANAGE_PERMISSIONS_REMOVE = "$MANAGE_PERMISSIONS.remove" // Allows removing permissions

    val defaults = listOf(SELF)

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