package ai.lail.api.dto.responses.users

import ai.lail.api.data.settings.user.UserSettings
import java.io.Serializable

/**
 * Data transfer object for the user settings.
 * All settings but [profilePrivacySetting] and [uploadPrivacySetting] are nullable.
 *
 * @property username The username of the user.
 * @property profilePrivacySetting The privacy setting for the user's profile as the enum value.
 * @property uploadPrivacySetting The privacy setting for the user's uploads as the enum value.
 * @property customUrlSlug The custom URL slug of the user.
 * @property profileColor The profile color of the user.
 * @property region The region of the user.
 * @property server The server of the user.
 */
class UserSettingsDto(settings: UserSettings) : Serializable {
    val username: String? = settings.username
    val profilePrivacySetting: Int = settings.profilePrivacySetting.id
    val uploadPrivacySetting: Int = settings.uploadPrivacySetting.id
    val customUrlSlug: String? = settings.customUrlSlug?.lowercase()
    val profileColor: String? = settings.profileColor
    val region: String? = settings.region?.uppercase()
    val server: String? = settings.server?.uppercase()
}