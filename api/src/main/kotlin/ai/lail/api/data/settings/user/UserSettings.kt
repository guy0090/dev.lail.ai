package ai.lail.api.data.settings.user

import ai.lail.api.dto.requests.users.UserSettingResetDto
import ai.lail.api.dto.requests.users.UserSettingsUpdateDto
import ai.lail.api.enums.PrivacySetting
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

/**
 * Represents the settings of a user in the database.
 *
 * @property id The ID of the user.
 * @property username The custom username of the user. Is not unique.
 * @property profilePrivacySetting The privacy setting of the user's profile.
 * @property uploadPrivacySetting The privacy setting of the user's uploads.
 * @property customUrlSlug The custom URL slug of the user's profile
 * @property profileColor The color of the user's character in uploads
 * @property region The region of the user's server
 * @property server The server of the user
 */
@Document("user_settings")
data class UserSettings(
    @Id
    var id: ObjectId = ObjectId(),

    var username: String? = null,

    @Field("profile_privacy_setting")
    var profilePrivacySetting: PrivacySetting = PrivacySetting.PUBLIC,

    @Field("upload_privacy_setting")
    var uploadPrivacySetting: PrivacySetting = PrivacySetting.PUBLIC,

    @Field("custom_url_slug")
    var customUrlSlug: String? = null,

    @Field("profile_color")
    var profileColor: String? = null,

    var region: String? = null,
    var server: String? = null,
) : Serializable {
    fun update(update: UserSettingsUpdateDto): Boolean {
        var changed = false

        if (update.username != null && update.username != username) {
            username = update.username
            changed = true
        }
        if (update.profilePrivacySetting != null && update.profilePrivacySetting != profilePrivacySetting) {
            profilePrivacySetting = update.profilePrivacySetting
            changed = true
        }
        if (update.uploadPrivacySetting != null && update.uploadPrivacySetting != uploadPrivacySetting) {
            uploadPrivacySetting = update.uploadPrivacySetting
            changed = true
        }
        if (update.customUrlSlug != null && update.customUrlSlug != customUrlSlug) {
            customUrlSlug = update.customUrlSlug
            changed = true
        }
        if (update.profileColor != null && update.profileColor != profileColor) {
            profileColor = update.profileColor
            changed = true
        }
        if (update.region != null && update.region != region) {
            region = update.region
            changed = true
        }
        if (update.server != null && update.server != server) {
            server = update.server
            changed = true
        }
        return changed
    }

    fun reset(update: UserSettingResetDto): Boolean {
        var changed = false

        if (update.username != null && update.username) {
            username = null
            changed = true
        }
        if (update.profilePrivacySetting != null && update.profilePrivacySetting) {
            profilePrivacySetting = PrivacySetting.PUBLIC
            changed = true
        }
        if (update.uploadPrivacySetting != null && update.uploadPrivacySetting) {
            uploadPrivacySetting = PrivacySetting.PUBLIC
            changed = true
        }
        if (update.customUrlSlug != null && update.customUrlSlug) {
            customUrlSlug = null
            changed = true
        }
        if (update.profileColor != null && update.profileColor) {
            profileColor = null
            changed = true
        }
        if (update.region != null && update.region) {
            region = null
            changed = true
        }
        if (update.server != null && update.server) {
            server = null
            changed = true
        }
        return changed
    }

    fun isPrivate(): Boolean {
        return profilePrivacySetting == PrivacySetting.PRIVATE
    }
}