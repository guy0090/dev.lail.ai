package ai.lail.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "discord")
data class DiscordProperties(
    /**
     * The client ID of the Discord application.
     */
    var clientId: String = "",

    /**
     * The client secret of the Discord application.
     */
    var clientSecret: String = "",

    /**
     * The redirect URI of the Discord application.
     */
    var redirectUri: String = "http://locoalhost:7070/login",

    /**
     * The redirect URI when registering/logging in with the app.
     * Port isn't required since Electron captures the redirect.
     */
    var redirectUriApp: String = "http://localhost/login",

    /**
     * The scope when accessing Discord APIs.
     */
    var scope: String = "identify",

    /**
     * The time in milliseconds that the user refresh token is valid for.
     */
    var userRefreshTimeout: Long = 86400000,

    /**
     * The user agent to use when accessing Discord APIs.
     */
    var agent: String = "LAL (Lost Ark Logs %version%)"
)