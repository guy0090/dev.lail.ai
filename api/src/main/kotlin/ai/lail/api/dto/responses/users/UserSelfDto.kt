package ai.lail.api.dto.responses.users

import ai.lail.api.data.users.User

/**
 * Data transfer object for the user requesting their own data.
 * @property id The ID of the user.
 * @property discordId The Discord ID of the user.
 * @property discordUsername The Discord username of the user.
 * @property discriminator The Discord discriminator of the user.
 * @property avatar The Discord avatar of the user.
 * @property registeredDate The date the user registered.
 * @property lastSeen The date the user last logged in.
 * @property banned Whether the user is banned.
 * @property uploads The current number of uploads a user has.
 * @property maxUploads The maximum number of uploads the user can have.
 */
class UserSelfDto(user: User, val uploads: Int) {
    val id: String = user.id.toHexString()
    val discordId = user.discordId
    val discordUsername = user.discordUsername
    val discriminator = user.discriminator
    val avatar: String? = user.avatar
    val registeredDate: Long = user.registeredDate
    val lastSeen: Long = user.lastSeen
    val banned: Boolean = user.banned
    val maxUploads: Int = user.maxUploads
}