package ai.lail.api.data.guilds

import ai.lail.api.permissions.nodes.UserGuildPermission
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.io.Serializable

/**
 * Data class for guild members. A guild member can have multiple roles.
 *
 * @property id The member's ID
 * @property nickname The member's nickname
 * @property permissions The member-specific permissions (See [UserGuildPermission])
 * @property roles IDs of a member's roles (See [GuildRole])
 * @property joined The member's join date
 */
data class GuildMember(
    @Id
    val id: ObjectId = ObjectId(),
    var nickname: String = "",
    val permissions: MutableSet<String> = mutableSetOf(),
    val roles: MutableSet<ObjectId> = mutableSetOf(),
    val joined: Number = System.currentTimeMillis(),
) : Serializable {

    /**
     * Create a guild member with only ID and nickname
     */
    constructor(id: ObjectId, nickname: String) : this(
        id,
        nickname,
        mutableSetOf(),
        mutableSetOf(GuildRole.getDefaultRole().id),
        System.currentTimeMillis()
    )

    /**
     * Add a permission to the member.
     * @param permission The permission to add.
     */
    fun addPermission(permission: String) {
        permissions.add(permission)
    }

    /**
     * Add multiple permissions to the member.
     * @param permission The permissions to add.
     */
    fun addPermissions(permission: List<String>) {
        permissions.addAll(permission.toSet())
    }

    /**
     * Remove a permission from the member.
     * @param permission The permission to remove.
     */
    fun removePermission(permission: String) {
        permissions.remove(permission)
    }

    /**
     * Remove multiple permissions from the member.
     * @param permission The permissions to remove.
     */
    fun removePermissions(permission: List<String>) {
        permissions.removeAll(permission.toSet())
    }

    /**
     * Clear all permissions from the member.
     */
    fun clearPermissions() {
        permissions.clear()
    }

    /**
     * Add a role to the member.
     * @param role The ID of the role to add.
     */
    fun addRole(role: ObjectId) {
        roles.add(role)
    }

    /**
     * Remove a role from the member.
     * @param role The ID of the role to remove.
     */
    fun removeRole(role: ObjectId) {
        roles.remove(role)
    }

    /**
     * Get all permissions of the member, including permissions from their roles.
     * @param roles The roles of the member.
     */
    fun getPermissions(roles: List<GuildRole>): Set<String> {
        val userPermissions = permissions
        roles.forEach { role -> userPermissions.addAll(role.permissions) }
        return userPermissions
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GuildMember) return false

        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}