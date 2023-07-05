package ai.lail.api.dto.requests.users

import ai.lail.api.enums.PrivacySetting
import jakarta.validation.constraints.Pattern

class UserSettingsUpdateDto {
    val profilePrivacySetting: PrivacySetting? = null
    val uploadPrivacySetting: PrivacySetting? = null

    @Pattern(
        regexp = "^\\S{2,32}$",
        message = "Custom username must be between 2 and 32 characters long and not contain spaces or other line breaks."
    )
    var username: String? = null

    @Pattern(
        regexp = "^[a-zA-Z0-9_]{2,20}$",
        message = "Custom URL slug must be alphanumeric with underscores and between 2 and 20 characters long."
    )
    val customUrlSlug: String? = null

    @Pattern(regexp = "^#[a-fA-F0-9]{6}$", message = "Profile color must be a valid hex color.")
    val profileColor: String? = null

    // TODO: Implement proper validation for regions/servers based on game information
    @Pattern(
        regexp = "^[a-zA-Z0-9]{2,10}$",
        message = "Region must be alphanumeric and between 2 and 10 characters long."
    )
    val region: String? = null

    @Pattern(
        regexp = "^[a-zA-Z0-9]{2,10}$",
        message = "Server must be alphanumeric and between 2 and 10 characters long."
    )
    val server: String? = null
}