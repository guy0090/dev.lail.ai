package ai.lail.api.permissions.nodes

import ai.lail.api.util.Reflect

object AdminPermission {
    const val ROOT = "admin"                                 // Allows access to all admin nodes
    const val PANEL = "$ROOT.panel"                          // Allows access to the admin panel
    const val REGISTRATION = "$ROOT.registration"            // Allows toggling registration (allows/disallows new users to register)
    const val UPLOADS = "$ROOT.uploads"                      // Allows disabling/enabling uploads (allows/disallows uploading of encounters)
    const val MAINTENANCE = "$ROOT.maintenance"              // Allows toggling maintenance mode (allows/disallows access to the instance)
    const val LIMITS = "$ROOT.limits"                        // Allows changing limits (all sub-nodes)
    const val LIMITS_USERS = "$LIMITS.users"                 // Allows changing the maximum number of users allowed on the instance
    const val LIMITS_USER_API_KEYS = "$LIMITS.user.api_keys" // Allows changing the maximum number of API keys allowed per user
    const val LIMITS_ROLES = "$LIMITS.encounters"            // Allows changing the maximum number of encounters allowed on the instance
    const val LIMITS_ENCOUNTERS = "$LIMITS.roles"            // Allows changing the maximum number of roles allowed on the instance
    const val LIMITS_GUILDS = "$LIMITS.guilds"               // Allows changing the maximum number of guilds allowed on the instance
    const val LIMITS_GUILD_MEMBERS = "$LIMITS.guild.members" // Allows changing the maximum number of members allowed in a guild
    const val LIMITS_GUILD_ROLES = "$LIMITS.guild.roles"     // Allows changing the maximum number of roles allowed in a guild
    const val DEFAULTS = "$ROOT.defaults"                    // Allows changing defaults (all sub-nodes)
    const val DEFAULTS_ROLE = "$DEFAULTS.role"               // Allows changing the default role for new users
    const val DEFAULTS_GUILD = "$DEFAULTS.guild"             // Allows changing the default guild for new users

    val defaults = emptyList<String>()

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
