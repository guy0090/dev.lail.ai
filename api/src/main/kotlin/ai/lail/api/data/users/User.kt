package ai.lail.api.data.users

import ai.lail.api.data.discord.DiscordUser
import ai.lail.api.enums.UserType
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

/**
 * Represents a user in the database.
 * @property id The ID of the user.
 * @property discordUsername The username of the user on Discord.
 * @property discordId The ID of the user on Discord.
 * @property discriminator The discriminator of the user.
 * @property avatar The avatar of the user.
 * @property registeredDate The date the user registered.
 * @property lastSeen The date the user was last seen.
 * @property banned Whether the user is banned.
 * @property lastDiscordUpdate The date the user's Discord profile was last updated.
 * @property blockedUsers The IDs of the users the user has blocked.
 */
@Document("users")
data class User(
    @Id
    @JsonSerialize(using = ToStringSerializer::class, `as` = String::class)
    var id: ObjectId = ObjectId(),

    @Field("discord_username")
    var discordUsername: String = "",

    @Field("discord_id")
    var discordId: String = "",
    var discriminator: String = "",
    var avatar: String? = null,
    @Field("registered_date")
    var registeredDate: Long = System.currentTimeMillis(),

    @Field("last_seen")
    var lastSeen: Long = System.currentTimeMillis(),
    var banned: Boolean = false,

    @Field("last_discord_update") @JsonIgnore
    var lastDiscordUpdate: Long = System.currentTimeMillis(),

    @Field("blocked_users")
    var blockedUsers: MutableSet<ObjectId> = mutableSetOf(),

    @Field("type")
    var userType: UserType = UserType.USER,

    @Field("max_uploads")
    var maxUploads: Int = 1000
) : Serializable {

    companion object {
        /**
         * Create a new [User] instance from a [DiscordUser].
         * @param discordUser The DiscordUser to create the User from.
         */
        fun fromDiscordUser(discordUser: DiscordUser, maxUploads: Int): User {
            val instance = User(maxUploads = maxUploads)
            instance.discordId = discordUser.id
            instance.discordUsername = discordUser.username
            instance.discriminator = discordUser.discriminator
            instance.avatar = discordUser.avatar
            return instance
        }
    }

    /**
     * Get the full Discord username of the user.
     *
     * The full Discord username has the format `username#discriminator`.
     */
    fun getFullDiscordUsername(): String {
        return "$discordUsername#$discriminator"
    }

    /**
     * Add a user to the list of blocked users.
     * @param userId The ID of the user to block.
     */
    fun addBlockedUser(userId: ObjectId) {
        blockedUsers.add(userId)
    }

    /**
     * Remove a user from the list of blocked users.
     * @param userId The ID of the user to unblock.
     */
    fun removeBlockedUser(userId: ObjectId) {
        blockedUsers.remove(userId)
    }

    /**
     * Check if a user is blocked.
     * @param userId The ID of the user to check.
     */
    fun hasUserBlocked(userId: ObjectId) = blockedUsers.contains(userId)

    fun updateFromDiscord(update: DiscordUser): User {
        discordUsername = update.username
        discriminator = update.discriminator
        avatar = update.avatar
        lastSeen = System.currentTimeMillis()
        lastDiscordUpdate = System.currentTimeMillis()

        return this
    }
}
