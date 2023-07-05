package ai.lail.api.data.settings.system

import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

/**
 * Represents the default limits of the application in the database.
 * @property maxUsers The maximum number of users.
 * @property maxRoles The maximum number of roles.
 * @property maxEncounters The maximum number of encounters.
 * @property maxGuilds The maximum number of guilds.
 * @property maxGuildMembers The maximum number of members per guild.
 * @property maxGuildRoles The maximum number of roles per guild.
 */
data class Limits(
    @Field("max_users")
    val maxUsers: Int = 10000,
    @Field("max_roles")
    val maxRoles: Int = 100,
    @Field("max_encounters")
    val maxEncounters: Int = 500000,
    @Field("max_guilds")
    val maxGuilds: Int = 100,
    @Field("max_guild_members")
    val maxGuildMembers: Int = 100,
    @Field("max_guild_roles")
    val maxGuildRoles: Int = 20
) : Serializable