package ai.lail.api.config.properties

import ai.lail.api.config.properties.shared.SslConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "vault")
class VaultProperties {

    var url: String = "https://localhost:8200"
    var token: String = "~/.loa/data/vault.backend.token"
    var ssl: SslConfiguration = SslConfiguration()
    var secretEngineVersion: Int = 2

}