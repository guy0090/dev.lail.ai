package ai.lail.api.dto.responses.users

import ai.lail.api.data.settings.user.UserSettings
import ai.lail.api.data.users.User
import ai.lail.api.enums.PrivacySetting
import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable

/**
 * Data transfer object for a user. Trims information that shouldn't be
 * publicly available and handles custom usernames.
 *
 * @param user The user to create the DTO from.
 * @param username The custom username of the user, can be null if none is set.
 *
 * @property id The ID of the user.
 * @property username The custom username of the user. Potentially null if not set.
 * @property avatar The avatar hash of the user from Discord. Can be null if the user has no avatar.
 * @property discordId The ID of the user on Discord.
 * @property discriminator The discriminator of the user.
 * @property lastSeen The last time the user was seen.
 */
class UserDto(user: User, settings: UserSettings) : Serializable {
    var id: String = user.id.toHexString()
    var username: String = settings.username ?: user.discordUsername
    @JsonIgnore
    var visibility: PrivacySetting = settings.profilePrivacySetting
    var avatar: String? = user.avatar
    var discordId: String = user.discordId
    var discriminator: String = user.discriminator
    var lastSeen: Long = user.lastSeen

    @JsonIgnore
    fun isPrivate(): Boolean {
        return visibility == PrivacySetting.PRIVATE
    }
}