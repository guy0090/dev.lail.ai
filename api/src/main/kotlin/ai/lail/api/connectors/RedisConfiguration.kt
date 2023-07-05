package ai.lail.api.connectors

import ai.lail.api.config.services.CacheConfigurationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfiguration : CachingConfigurer {
    val logger: Logger = LoggerFactory.getLogger(RedisConfiguration::class.java)

    private fun createCacheConfiguration(timeoutInSeconds: Long): RedisCacheConfiguration {
        val ttl = if (timeoutInSeconds <= 0) Duration.ZERO else Duration.ofSeconds(timeoutInSeconds)
        return RedisCacheConfiguration.defaultCacheConfig().entryTtl(ttl)
    }

    @Bean
    fun redisConnectionFactory(properties: CacheConfigurationService): LettuceConnectionFactory? {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = properties.getRedisHost()
        redisStandaloneConfiguration.port = properties.getRedisPort()
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    @Primary
    fun redisTemplate(cf: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setConnectionFactory(cf)
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.keySerializer = StringRedisSerializer()
        return redisTemplate
    }

    @Bean
    fun cacheConfiguration(properties: CacheConfigurationService): RedisCacheConfiguration {
        return createCacheConfiguration(properties.getDefaultTimeout())
    }

    @Bean
    fun cacheManager(
        redisConnectionFactory: RedisConnectionFactory,
        properties: CacheConfigurationService
    ): CacheManager {
        val cacheConfigurations: HashMap<String, RedisCacheConfiguration> = HashMap()
        for (cacheNameAndTimeout in properties.getExpirationMap().entries) {
            cacheConfigurations[cacheNameAndTimeout.key] = createCacheConfiguration(cacheNameAndTimeout.value)
            logger.info("Cache '${cacheNameAndTimeout.key}' will expire after ${cacheNameAndTimeout.value} seconds")
        }

        return RedisCacheManager
            .builder(redisConnectionFactory)
            .cacheDefaults(cacheConfiguration(properties))
            .withInitialCacheConfigurations(cacheConfigurations).build()
    }
}