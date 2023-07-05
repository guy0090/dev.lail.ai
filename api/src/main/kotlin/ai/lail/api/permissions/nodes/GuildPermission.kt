package ai.lail.api.permissions.nodes

import ai.lail.api.util.Reflect

/**
 * Permission nodes for guilds.
 */
object GuildPermission {
    const val ROOT = "guilds"
    const val LIST = "$ROOT.list"
    const val VIEW = "$ROOT.view"
    const val APPLY = "$ROOT.apply"
    const val CREATE = "$ROOT.create"
    const val DELETE = "$ROOT.delete"
    const val MANAGE = "$ROOT.manage"
    const val MANAGE_LIST = "$MANAGE.list"
    const val MANAGE_DELETE = "$MANAGE.delete"
    const val MANAGE_EDIT = "$MANAGE.edit"
    const val MANAGE_VIEW = "$MANAGE.view"
    const val MANAGE_ROLES = "$MANAGE.roles"
    const val MANAGE_ROLES_ADD = "$MANAGE_ROLES.add"
    const val MANAGE_ROLES_REMOVE = "$MANAGE_ROLES.remove"
    const val MANAGE_ROLES_PERMISSIONS = "$MANAGE_ROLES.permissions"
    const val MANAGE_ROLES_PERMISSIONS_ADD = "$MANAGE_ROLES_PERMISSIONS.add"
    const val MANAGE_ROLES_PERMISSIONS_REMOVE = "$MANAGE_ROLES_PERMISSIONS.remove"
    const val MANAGE_MEMBERS = "$MANAGE.members"
    const val MANAGE_MEMBERS_ADD = "$MANAGE_MEMBERS.add"
    const val MANAGE_MEMBERS_REMOVE = "$MANAGE_MEMBERS.remove"
    const val MANAGE_MEMBERS_PERMISSIONS = "$MANAGE_MEMBERS.permissions"
    const val MANAGE_MEMBERS_PERMISSIONS_ADD = "$MANAGE_MEMBERS_PERMISSIONS.add"
    const val MANAGE_MEMBERS_PERMISSIONS_REMOVE = "$MANAGE_MEMBERS_PERMISSIONS.remove"
    const val MANAGE_MEMBERS_ROLES = "$MANAGE_MEMBERS.roles"
    const val MANAGE_MEMBERS_ROLES_ADD = "$MANAGE_MEMBERS_ROLES.add"
    const val MANAGE_MEMBERS_ROLES_REMOVE = "$MANAGE_MEMBERS_ROLES.remove"

    val defaults = listOf(LIST, VIEW, APPLY, CREATE)

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