package ai.lail.api.config.services

import ai.lail.api.config.properties.shared.SystemProperties
import org.springframework.stereotype.Service

@Service
class SystemConfigurationService(val props: SystemProperties) {

    fun getHost() = props.host
    fun usingTls() = props.tls
    fun getFrontendHost() = props.frontend.host
    fun getFrontendPort() = props.frontend.port

    fun getFrontendUrl() =
        "http${if (usingTls()) "s" else ""}://${getFrontendHost()}${if (!usingTls()) ":${getFrontendPort()}" else ""}"
}