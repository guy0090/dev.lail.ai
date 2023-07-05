package ai.lail.api.config.properties.shared

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "system")
class SystemProperties {

    var host: String = "localhost"
    var tls: Boolean = false
    var frontend: Frontend = Frontend()
}