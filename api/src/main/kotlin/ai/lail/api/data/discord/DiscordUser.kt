package ai.lail.api.data.discord

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a Discord user as returned from the Discord API.
 * @property id The user's ID.
 * @property username The user's username, not unique across the platform.
 * @property discriminator The user's 4-digit discord-tag.
 * @property avatar The user's avatar hash.
 * @property bot Whether the user belongs to an OAuth2 application.
 * @property system Whether the user is an Official Discord System user (part of the urgent message system).
 * @property mfaEnabled Whether the user has two factor enabled on their account.
 * @property banner The user's banner hash.
 * @property accentColor The user's chosen accent color hex.
 * @property locale The user's chosen language option.
 * @property verified Whether the email on this account has been verified.
 * @property email The user's email.
 * @property flags The flags on a user's account.
 * @property premiumType The type of Nitro subscription on a user's account.
 * @property publicFlags The public flags on a user's account.
 * @see <a href="https://discord.com/developers/docs/resources/user#user-object-user-structure">Discord User</a>
 */
data class DiscordUser(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("username")
    val username: String,
    @JsonProperty("discriminator")
    val discriminator: String,
    @JsonProperty("avatar")
    val avatar: String? = null,
    @JsonProperty("bot")
    val bot: Boolean? = false,
    @JsonProperty("system")
    val system: Boolean? = false,
    @JsonProperty("mfa_enabled")
    val mfaEnabled: Boolean? = false,
    @JsonProperty("banner")
    val banner: String? = "",
    @JsonProperty("accent_color")
    val accentColor: Long? = 0,
    @JsonProperty("locale")
    val locale: String? = "",
    @JsonProperty("verified")
    val verified: Boolean? = false,
    @JsonProperty("email")
    val email: String? = "",
    @JsonProperty("flags")
    val flags: Int? = 0,
    @JsonProperty("premium_type")
    val premiumType: Int? = 0,
    @JsonProperty("public_flags")
    val publicFlags: Int? = 0,
)