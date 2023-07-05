package ai.lail.api.permissions.nodes

import ai.lail.api.util.Reflect

object UserPermission {
    const val ROOT = "users"                                           // Allows access to all user nodes
    const val VIEW = "$ROOT.view"                                      // Allows viewing users
    const val VIEW_MANY = "$VIEW.many"                                 // Allows viewing many users
    const val SELF = "$ROOT.self"                                      // Allows operations on self
    const val SELF_VIEW = "$SELF.view"                                 // Allows viewing self
    const val SELF_EDIT = "$SELF.edit"                                 // Allows editing self
    const val SELF_DELETE = "$SELF.delete"                             // Allows deleting self
    const val SELF_SETTINGS = "$SELF.settings"                         // Allows managing settings for self
    const val SELF_SETTINGS_VIEW = "$SELF_SETTINGS.view"               // Allows viewing settings for self
    const val SELF_SETTINGS_EDIT = "$SELF_SETTINGS.edit"               // Allows editing settings for self
    const val SELF_KEYS = "$SELF.keys"                                 // Allows managing keys for self
    const val SELF_KEYS_VIEW = "$SELF_KEYS.view"                       // Allows viewing keys for self
    const val SELF_KEYS_ADD = "$SELF_KEYS.add"                         // Allows adding keys for self
    const val SELF_KEYS_REMOVE = "$SELF_KEYS.remove"                   // Allows removing keys for self
    const val SELF_BLOCKED = "$SELF.blocked"                           // Allows managing block list for self
    const val SELF_BLOCKED_VIEW = "$SELF_BLOCKED.view"                 // Allows viewing block list for self
    const val SELF_BLOCKED_ADD = "$SELF_BLOCKED.add"                   // Allows adding to block list for self
    const val SELF_BLOCKED_REMOVE = "$SELF_BLOCKED.remove"             // Allows removing from block list for self

    // Administrative permissions
    const val MANAGE = "$ROOT.manage"                                  // Allows managing users
    const val MANAGE_VIEW = "$MANAGE.view"                             // Allows viewing users regardless privacy settings
    const val MANAGE_EDIT = "$MANAGE.edit"                             // Allows editing users
    const val MANAGE_DELETE = "$MANAGE.delete"                         // Allows deleting users
    const val MANAGE_KEYS = "$MANAGE.keys"                             // Allows managing keys for users
    const val MANAGE_KEYS_ADD = "$MANAGE_KEYS.add"                     // Allows adding keys for users
    const val MANAGE_KEYS_REMOVE = "$MANAGE_KEYS.remove"               // Allows removing keys for users
    const val MANAGE_BLOCKED = "$MANAGE.blocked"                       // Allows managing block list for users
    const val MANAGE_BLOCKED_VIEW = "$MANAGE_BLOCKED.view"             // Allows viewing block list for users
    const val MANAGE_BLOCKED_ADD = "$MANAGE_BLOCKED.add"               // Allows adding to block list for users
    const val MANAGE_BLOCKED_REMOVE = "$MANAGE_BLOCKED.remove"         // Allows removing from block list for users
    const val MANAGE_MODERATION = "$MANAGE.moderation"                 // Allows moderation of users
    const val MANAGE_MODERATION_BAN = "$MANAGE_MODERATION.ban"         // Allows banning users
    const val MANAGE_MODERATION_UNBAN = "$MANAGE_MODERATION.unban"     // Allows unbanning users
    const val MANAGE_MODERATION_MUTE = "$MANAGE_MODERATION.mute"       // Allows muting users (making comments)
    const val MANAGE_MODERATION_UNMUTE = "$MANAGE_MODERATION.unmute"   // Allows unmuting users (making comments)
    const val MANAGE_ROLES = "$MANAGE.roles"                           // Allows managing roles for users
    const val MANAGE_ROLES_ADD = "$MANAGE_ROLES.add"                   // Allows adding roles for users
    const val MANAGE_ROLES_REMOVE = "$MANAGE_ROLES.remove"             // Allows removing roles for users
    const val MANAGE_PERMISSIONS = "$MANAGE.permissions"               // Allows managing permissions for users
    const val MANAGE_PERMISSIONS_ADD = "$MANAGE_PERMISSIONS.add"       // Allows adding permissions for users
    const val MANAGE_PERMISSIONS_REMOVE = "$MANAGE_PERMISSIONS.remove" // Allows removing permissions for users

    val defaults = listOf(VIEW, SELF)

    fun getAllPermissions(): Set<String> = Reflect.getAllConstValues(this::class)

    fun isValidPermission(permission: String): Boolean = getAllPermissions().contains(permission)

    fun areValidPermissions(permissions: List<String>): Boolean = getAllPermissions().containsAll(permissions)
}