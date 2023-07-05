package ai.lail.api.services

import ai.lail.api.config.services.DiscordConfigurationService
import ai.lail.api.config.services.ProjectConfigurationService
import ai.lail.api.data.discord.DiscordOAuthGrant
import ai.lail.api.data.discord.DiscordUser
import ai.lail.api.enums.OAuthClient
import ai.lail.api.exceptions.discord.DiscordGetGrantException
import ai.lail.api.exceptions.discord.DiscordGetUserException
import ai.lail.api.exceptions.discord.DiscordRefreshGrantException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

/**
 * Helper class for Discord OAuth.
 */
@Service
class DiscordApiService(
    final val projectConfigurationService: ProjectConfigurationService,
    final val discordConfigurationService: DiscordConfigurationService
) {
    val logger: Logger = LoggerFactory.getLogger(DiscordApiService::class.java)

    /**
     * The amount of time (in milliseconds) that must have passed since the last update
     * before the user's Discord information is refreshed.
     */
    val userRefreshTimeout: Long = discordConfigurationService.getUserRefreshTimeoutMillis()

    private val clientId: String = discordConfigurationService.getClientId()
    private val clientSecret: String = discordConfigurationService.getClientSecret()
    private val redirectUri: String = discordConfigurationService.getRedirectUri()
    private val redirectUriApp: String = discordConfigurationService.getRedirectUriApp()
    private val scope: String = discordConfigurationService.getScope()
    private val discordApi: String = "https://discord.com/api/v10"
    private val agent =
        discordConfigurationService.getAgent().replace("%version%", projectConfigurationService.getVersion())
    private val apiClient: WebClient = WebClient.builder()
        .defaultHeader(HttpHeaders.USER_AGENT, agent)
        .baseUrl(discordApi)
        .build()

    /**
     * Gets the access token.
     * @param code The code to exchange for an access token.
     */
    @Throws(DiscordGetUserException::class)
    fun getToken(code: String, client: OAuthClient = OAuthClient.WEB): DiscordOAuthGrant? {
        val redirect = if (client == OAuthClient.WEB) redirectUri else redirectUriApp
        val body = BodyInserters.fromFormData("client_id", clientId)
            .with("client_secret", clientSecret)
            .with("grant_type", "authorization_code")
            .with("code", code)
            .with("redirect_uri", redirect)
            .with("scope", scope)

        return try {
            apiClient.post()
                .uri("/oauth2/token")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .exchangeToMono { it.bodyToMono(DiscordOAuthGrant::class.java) }
                .block()
        } catch (e: Exception) {
            throw DiscordGetGrantException(e)
        }
    }

    /**
     * Refreshes the access token.
     * @param refreshToken The refresh token.
     */
    fun refreshToken(refreshToken: String): DiscordOAuthGrant? {
        val body = BodyInserters.fromFormData("client_id", clientId)
            .with("client_secret", clientSecret)
            .with("grant_type", "refresh_token")
            .with("refresh_token", refreshToken)

        return try {
            apiClient.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .exchangeToMono { it.bodyToMono(DiscordOAuthGrant::class.java) }
                .block()
        } catch (e: Exception) {
            throw DiscordRefreshGrantException(e)
        }
    }

    /**
     * Gets the user from the Discord API.
     * @param accessToken The access token.
     * @return The user.
     * @see <a href="https://discord.com/developers/docs/resources/user#user-object">Discord API</a>
     */
    fun getUser(accessToken: String): DiscordUser? {
        return try {
            apiClient.get()
                .uri("/users/@me")
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .bodyToMono(DiscordUser::class.java)
                .block()
        } catch (e: Exception) {
            throw DiscordGetUserException(e)
        }
    }
}