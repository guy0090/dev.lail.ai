package ai.lail.api.config.properties

import ai.lail.api.config.properties.shared.Expiration
import ai.lail.api.config.properties.shared.Redis
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "cache")
data class CacheProperties(

    /**
     * The default timeout for all cacheable services
     */
    var timeout: Long = 60,

    /**
     * The redis configuration
     */
    var redis: Redis = Redis(),

    /**
     * The expiration settings for all cacheable services
     */
    var expiration: Expiration = Expiration()

)