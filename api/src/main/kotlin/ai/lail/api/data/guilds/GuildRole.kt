package ai.lail.api.data.guilds

import ai.lail.api.permissions.nodes.UserGuildPermission
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.io.Serializable

/**
 * A role in a guild.
 *
 * @property id The id of the role.
 * @property name The name of the role.
 * @property color The color of the role.
 * @property permissions The permissions of the role.
 *
 * @see UserGuildPermission
 */
data class GuildRole(
    @Id
    val id: ObjectId = ObjectId(),
    var name: String = "Guild Role",
    var color: String = "#fff",
    val permissions: MutableSet<String> = mutableSetOf(),
) : Serializable {
    companion object {

        /**
         * The default role for a guild.
         */
        val defaultRoleId = ObjectId("000000000000000000000000")

        /**
         * Get the default role for a guild. Sets permissions based on [UserGuildPermission.defaults].
         */
        fun getDefaultRole() =
            GuildRole(defaultRoleId, "Member", "#ff0000", UserGuildPermission.defaults.toMutableSet())
    }

    /**
     * Add a permission to the role.
     * @param permission The permission to add.
     */
    fun addPermission(permission: String) {
        permissions.add(permission)
    }

    /**
     * Add multiple permissions to the role.
     * @param permission The permissions to add.
     */
    fun addPermissions(permission: List<String>) {
        permissions.addAll(permission)
    }

    /**
     * Remove a permission from the role.
     * @param permission The permission to remove.
     */
    fun removePermission(permission: String) {
        permissions.remove(permission)
    }

    /**
     * Remove multiple permissions from the role.
     * @param permission The permissions to remove.
     */
    fun removePermissions(permission: List<String>) {
        permissions.removeAll(permission.toSet())
    }

    /**
     * Check if the role has a permission.
     * @param permission The permission to check.
     */
    fun hasPermission(permission: String): Boolean {
        return permissions.contains(permission)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GuildRole) return false

        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
