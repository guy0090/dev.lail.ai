package ai.lail.api.permissions.nodes

import ai.lail.api.util.Reflect

/**
 * Permissions available within the scope of a Guild.
 */
object UserGuildPermission {
    const val ROOT = "guild"
    const val MEMBERS = "$ROOT.members"
    const val FILTER = "$ROOT.filter"
    const val VIEW = "$ROOT.view"
    const val ASSOCIATE = "$ROOT.associate"
    const val MANAGE = "$ROOT.manage"
    const val MANAGE_ROLES = "$ROOT.manage.roles"
    const val MANAGE_ROLES_ADD = "$ROOT.manage.roles.add"
    const val MANAGE_ROLES_REMOVE = "$ROOT.manage.roles.remove"
    const val MANAGE_ROLES_EDIT = "$ROOT.manage.roles.edit"
    const val MANAGE_PERMISSIONS = "$ROOT.manage.permissions"
    const val MANAGE_PERMISSIONS_ADD = "$ROOT.manage.permissions.add"
    const val MANAGE_PERMISSIONS_REMOVE = "$ROOT.manage.permissions.remove"
    const val MANAGE_PERMISSIONS_EDIT = "$ROOT.manage.permissions.edit"
    const val MANAGE_MEMBERS = "$ROOT.manage.members"
    const val MANAGE_MEMBERS_INVITE = "$ROOT.manage.members.invite"
    const val MANAGE_MEMBERS_ACCEPT = "$ROOT.manage.members.accept"
    const val MANAGE_MEMBERS_DENY = "$ROOT.manage.members.deny"
    const val MANAGE_MEMBERS_KICK = "$ROOT.manage.members.kick"
    const val MANAGE_MEMBERS_BAN = "$ROOT.manage.members.ban"

    val defaults = listOf(MEMBERS, FILTER, VIEW, ASSOCIATE)

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
