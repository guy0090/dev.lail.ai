package ai.lail.api.services

import ai.lail.api.config.services.ApiKeyConfigurationService
import ai.lail.api.config.services.CacheConfigurationService
import ai.lail.api.config.services.JwtConfigurationService
import ai.lail.api.connectors.VaultConfiguration
import ai.lail.api.data.vault.UserSecrets
import ai.lail.api.util.CacheHelper
import ai.lail.api.util.CryptoUtil
import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultException
import com.bettercloud.vault.response.LogicalResponse
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
@CacheConfig(cacheNames = [CacheHelper.VAULT_CACHE])
class VaultService(
    val vaultConfig: VaultConfiguration,
    val jwtConfigurationService: JwtConfigurationService,
    val apiKeyConfigurationService: ApiKeyConfigurationService,
    val redisTemplate: RedisTemplate<String, Any>,
    val cacheConfig: CacheConfigurationService
) {
    val logger: Logger = LoggerFactory.getLogger(VaultService::class.java)

    fun vault(): Vault {
        return vaultConfig.vault()
    }

    fun isReady(): Boolean = vaultConfig.ready

    // Users
    @Cacheable(key = "#user", unless = "#result == null")
    fun createUserSecrets(user: ObjectId): Map<String, String> {
        val salt = CryptoUtil.randomString(apiKeyConfigurationService.getSaltSize())
        val data = mapOf(SALT to salt)
        write(userSecrets(user), data)
        return data
    }

    @CacheEvict(key = "#user")
    fun setUserSecret(user: ObjectId, field: String, value: String): UserSecrets {
        val current = getUserSecrets(user) ?: throw VaultException("User secrets not found")
        val update = current.toMutableMap()
        val path = userSecrets(user)

        update[field] = value
        write(path, update)
        return UserSecrets(update)
    }

    fun getUserSecrets(user: ObjectId): Map<String, String>? {
        val path = userSecrets(user)
        val key = "${CacheHelper.VAULT_CACHE}::$user"

        redisTemplate.opsForValue().get(key)?.let {
            return it as Map<String, String>
        }

        val secrets = read(path)
        if (secrets != null) {
            val ttl = cacheConfig.getExpirations().vault
            redisTemplate.opsForValue().setIfAbsent(key, secrets, ttl, TimeUnit.SECONDS)
        }

        return secrets
    }

    fun getUserSalt(user: ObjectId): String? = getUserSecrets(user)?.get(SALT)

    @CacheEvict(key = "#user")
    fun resetUserSalt(user: ObjectId) {
        val newSalt = CryptoUtil.randomString(apiKeyConfigurationService.getSaltSize())
        setUserSecret(user, SALT, newSalt)
    }

    @CacheEvict(key = "#user")
    fun deleteUserSecrets(user: ObjectId) {
        val path = userSecrets(user)
        delete(path)
    }

    // Init Key
    fun createInitKey(): String {
        val initKey = CryptoUtil.randomString(32)
        val data = mapOf(INIT_KEY to initKey)
        write("$SYSTEM/$INIT_KEY", data)
        return initKey
    }

    fun getInitKey(): String? = read("$SYSTEM/$INIT_KEY")?.get(INIT_KEY)
    fun deleteInitKey() = delete("$SYSTEM/$INIT_KEY")

    // JWT

    /**
     * Creates a new JWT signing key that will be used to sign JWTs.
     */
    fun createJwtKey(): Boolean {
        if (getJwtSigningKey() != null) return false
        val jwtKey = CryptoUtil.randomString(jwtConfigurationService.getKeySize())
        val data = mapOf(SIGNING_KEY to jwtKey)
        write(JWT, data)
        return true
    }

    @CacheEvict(key = "'jwt'")
    fun setJwtKey() {
        val jwtKey = CryptoUtil.randomString(jwtConfigurationService.getKeySize())
        val data = mapOf(SIGNING_KEY to jwtKey)
        write(JWT, data)
    }

    @Cacheable(key = "'jwt'", unless = "#result == null")
    fun getJwtSigningKey(): String? = read(JWT)?.get(SIGNING_KEY)

    fun saveIngestToken(token: String) {
        val data = mapOf(INGEST_TOKEN to token)
        write("$SYSTEM/$INGEST_TOKEN", data)
    }

    fun getIngestToken(): String? = read("$SYSTEM/$INGEST_TOKEN")?.get(INGEST_TOKEN)

    // Vault API helpers
    fun write(path: String, data: Map<String, Any>): LogicalResponse = vault().logical().write(path, data)

    fun read(path: String): Map<String, String>? = vault().logical().read(path).data

    fun list(path: String): List<String> = vault().logical().list(path).listData

    fun delete(path: String): LogicalResponse = vault().logical().delete(path)

    // Constants
    companion object {
        private const val SECRET = "secret"
        private const val USER = "$SECRET/user"
        private const val SYSTEM = "$SECRET/system"
        private const val JWT = "$SYSTEM/jwt"

        const val SALT = "salt"
        const val INIT_KEY = "initKey"
        const val SIGNING_KEY = "signingKey"
        const val INGEST_TOKEN = "ingestToken"

        fun userSecrets(user: ObjectId) = "$USER/${user.toHexString()}"
    }

}