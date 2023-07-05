package ai.lail.api.config.services

import ai.lail.api.config.properties.DiscordProperties
import org.springframework.stereotype.Service

@Service
class DiscordConfigurationService(val props: DiscordProperties) {

    fun getClientId() = props.clientId
    fun getClientSecret() = props.clientSecret
    fun getRedirectUri() = props.redirectUri
    fun getRedirectUriApp() = props.redirectUriApp
    fun getScope() = props.scope
    fun getUserRefreshTimeoutMillis() = props.userRefreshTimeout
    fun getUserRefreshTimeoutSeconds() = props.userRefreshTimeout / 1000L
    fun getAgent() = props.agent

}