package ai.lail.api.data.discord

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a Discord OAuth grant.
 * @property accessToken The access token that can be used to authorize requests to the Discord API.
 * @property expiresIn The number of seconds until the access token expires.
 * @property refreshToken The refresh token that can be used to refresh the access token.
 * @property scope The scope of the access token.
 * @property tokenType The type of the token.
 * @see <a href="https://discord.com/developers/docs/topics/oauth2#authorization-code-grant-access-token-response">Discord OAuth2</a>
 */
data class DiscordOAuthGrant(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
    @JsonProperty("refresh_token")
    val refreshToken: String,
    @JsonProperty("scope")
    val scope: String,
    @JsonProperty("token_type")
    val tokenType: String
)