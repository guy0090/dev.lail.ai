package ai.lail.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "mongodb")
class MongoProperties {

    /**
     * The port of the MongoDB server.
     */
    var port: Int = 27017

    /**
     * The host of the MongoDB server.
     */
    var host: String = "mongo.service.loa"

    /**
     * Whether the MongoDB server requires authentication.
     */
    var auth: Boolean = false

}