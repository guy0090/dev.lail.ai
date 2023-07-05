package ai.lail.api.config.services

import ai.lail.api.config.properties.ApiKeyProperties
import org.springframework.stereotype.Service

@Service
class ApiKeyConfigurationService(val props: ApiKeyProperties) {

    fun getHeaderName() = props.header
    fun getKeyLength() = props.length
    fun getSalt() = props.salt
    fun getSaltSize() = getSalt().length
    fun getSaltHasher() = getSalt().hasher

}