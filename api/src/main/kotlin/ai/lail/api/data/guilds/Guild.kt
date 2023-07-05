package ai.lail.api.data.guilds

import ai.lail.api.enums.PrivacySetting
import ai.lail.api.exceptions.guilds.GuildRoleRemoveException
import ai.lail.api.permissions.nodes.UserGuildPermission
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

/**
 * Data class for guilds. A guild can have multiple members, each with their own roles.
 * A guild can create their own roles, each with their own permissions. (See [UserGuildPermission])
 *
 * @property id The guild's ID
 * @property name The guild's name
 * @property icon The guild's icon
 * @property owner The guild's owner
 * @property members The guild's members (See [GuildMember])
 * @property roles The guild's roles (See [GuildRole])
 * @property created The guild's creation date
 * @property privacy The guild's privacy setting (See [PrivacySetting])
 */
@Document("guilds")
data class Guild(
    @Id
    val id: ObjectId = ObjectId(),
    var name: String = "", // TODO: Automatic? Random series of LostArk words? Dictionary where?
    var icon: String = "", // TODO: Base64 or URL? Random icon from game?
    var owner: ObjectId,
    val members: MutableSet<GuildMember> = mutableSetOf(),
    val roles: MutableSet<GuildRole> = mutableSetOf(GuildRole.getDefaultRole()),
    val created: Number = System.currentTimeMillis(),
    var privacy: PrivacySetting = PrivacySetting.PRIVATE,
) : Serializable {

    constructor(owner: ObjectId) : this(ObjectId(), "", "", owner)

    /**
     * Add a member to the guild.
     * @param member The member to add.
     */
    fun addMember(member: GuildMember) {
        members.add(member)
    }

    /**
     * Remove a member from the guild.
     * @param memberId The ID of the member to remove.
     */
    fun removeMember(memberId: ObjectId) {
        members.removeIf { it.id == memberId }
    }

    /**
     * Gets a member by their ID.
     * @param id The ID of the member to check.
     */
    fun getMemberById(id: ObjectId): GuildMember? {
        return members.find { it.id == id }
    }

    /**
     * Gets a member by their nickname.
     * @param nickname The nickname to check for.
     */
    fun getMemberByNickname(nickname: String): GuildMember? {
        return members.find { it.nickname == nickname }
    }

    /**
     * Gets a list of members if they have a specific role.
     * @param role The role to check for.
     */
    fun getMembersByRole(role: ObjectId): List<GuildMember> {
        return members.filter { it.roles.contains(role) }
    }

    /**
     * Check if a member is in the guild.
     * @param memberId The ID of the member to check.
     */
    fun hasMember(memberId: ObjectId): Boolean {
        return members.any { it.id == memberId }
    }

    /**
     * Add a role to the guild.
     * @param role The role to add.
     */
    fun addRole(role: GuildRole) {
        roles.add(role)
    }

    /**
     * Remove a role from the guild.
     * @param roleId The ID of the role to remove.
     * @throws GuildRoleRemoveException if the role is the default role or the last role in the guild.
     */
    fun removeRole(roleId: ObjectId) {
        if (roles.size == 1) {
            throw GuildRoleRemoveException("Cannot remove last Role - a guild must have at least one role.")
        } else if (roleId == GuildRole.defaultRoleId) {
            throw GuildRoleRemoveException("The default role cannot be removed.")
        }
        roles.removeIf { it.id == roleId }
    }

    /**
     * Check if a guild has a specified role.
     * @param roleId The ID of the role to check.
     */
    fun hasRole(roleId: ObjectId): Boolean {
        return roles.any { it.id == roleId }
    }

    /**
     * Check if a guild has the specified roles.
     * @param roleIds The IDs of the roles to check.
     */
    fun hasRoles(roleIds: List<ObjectId>): Boolean {
        return roles.all { roleIds.contains(it.id) }
    }

}
