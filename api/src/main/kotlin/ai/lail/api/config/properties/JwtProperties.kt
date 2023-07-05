package ai.lail.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(

    /**
     * The name of the cookie that the JWT will be stored in.
     */
    var name: String = "at1",

    /**
     * The size of the secret key used to sign the JWT.
     * Will be automatically generated and saved to Vault
     * on first startup.
     */
    var keySize: Int = 64,

    /**
     * The time in seconds that the JWT is valid for.
     * Defaults to 1 year.
     */
    var expiration: Int = 60 * 60 * 24 * 365,

    /**
     * The issuer of the JWT.
     * Defaults to "lail.ai"
     */
    var issuer: String = "ai.lail",

    /**
     * The subject of the JWT.
     * Defaults to "LAL-AT"
     */
    var subject: String = "LAL-AT",

    )