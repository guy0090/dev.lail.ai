package ai.lail.api.config.services

import ai.lail.api.config.properties.JwtProperties
import org.springframework.stereotype.Service

@Service
class JwtConfigurationService(val props: JwtProperties) {

    fun getPrincipalName() = props.name
    fun getKeySize() = props.keySize
    fun getExpirationSeconds() = props.expiration
    fun getExpirationMillis() = props.expiration * 1000L
    fun getIssuer() = props.issuer
    fun getSubject() = props.subject

}