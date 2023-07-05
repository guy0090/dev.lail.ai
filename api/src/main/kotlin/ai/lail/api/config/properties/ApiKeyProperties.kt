package ai.lail.api.config.properties

import ai.lail.api.config.properties.shared.Salt
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "api-key")
class ApiKeyProperties {

    /**
     * The name of the header that the API key will be stored in (and grabbed from in the request).
     */
    var header: String = "X-API-KEY"

    /**
     * The length of the API key. Defaults to 32 (256 bit).
     */
    var length: Int = 32

    /**
     * The salt used to hash the API key.
     */
    var salt: Salt = Salt()

}