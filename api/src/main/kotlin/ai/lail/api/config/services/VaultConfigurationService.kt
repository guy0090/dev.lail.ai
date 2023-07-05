package ai.lail.api.config.services

import ai.lail.api.config.properties.VaultProperties
import org.springframework.stereotype.Service
import java.io.File

@Service
class VaultConfigurationService(val props: VaultProperties) {

    fun getUrl() = props.url
    fun getVaultToken() = File(props.token).readText().trim()
    fun getSsl() = props.ssl
    fun getCaCert() = File(getSsl().ca.cert)
    fun isSslEnabled() = getSsl().enabled
    fun getSecretEngineVersion() = props.secretEngineVersion

}