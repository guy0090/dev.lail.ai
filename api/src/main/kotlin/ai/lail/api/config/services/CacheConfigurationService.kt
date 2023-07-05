package ai.lail.api.config.services

import ai.lail.api.config.properties.CacheProperties
import org.springframework.stereotype.Service

@Service
class CacheConfigurationService(val properties: CacheProperties) {

    fun getDefaultTimeout() = properties.timeout
    fun getRedisConfig() = properties.redis
    fun getRedisHost() = getRedisConfig().host
    fun getRedisPort() = getRedisConfig().port
    fun getExpirations() = properties.expiration
    fun getExpirationMap() = getExpirations().toMap()

}