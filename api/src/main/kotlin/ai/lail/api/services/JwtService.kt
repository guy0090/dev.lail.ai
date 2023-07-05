package ai.lail.api.services

import ai.lail.api.config.services.JwtConfigurationService
import ai.lail.api.data.jwt.JwtPayload
import ai.lail.api.data.users.User
import ai.lail.api.util.CryptoUtil
import com.bettercloud.vault.VaultException
import io.fusionauth.jwt.HexUtils
import io.fusionauth.jwt.InvalidJWTSignatureException
import io.fusionauth.jwt.JWTExpiredException
import io.fusionauth.jwt.JWTUnavailableForProcessingException
import io.fusionauth.jwt.domain.JWT
import io.fusionauth.jwt.hmac.HMACSigner
import io.fusionauth.jwt.hmac.HMACVerifier
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Service for generating and decoding JWTs.
 *
 * All JWT are verified with a secret and expire after a certain amount of time.
 * - The secret is taken from [VaultService.getJwtSigningKey]
 * - The expiration time is taken from [JwtConfigurationService.getExpirationSeconds]
 *
 * The payload of the JWT contains a user id and a hash (user ID salted).
 * This is used to associate the JWT with a user in followup requests.
 */
@Service
class JwtService(
    val jwtConfigurationService: JwtConfigurationService,
    val userService: UserService,
    val vault: VaultService
) {
    private val logger: Logger = LoggerFactory.getLogger(JwtService::class.java)

    /**
     * Generates a JWT for a user.
     *
     * The payload contains the user id and a hash (user ID salted) for
     * later verification/association. See [JwtPayload] for more information.
     *
     * The token is signed with a secret and expires according to the value
     * in [JwtConfigurationService.getExpirationSeconds]
     */
    fun generateToken(userId: ObjectId): String {
        val salt = vault.getUserSalt(userId) ?: throw VaultException("Could not get salt for user $userId")
        val expirationDate =
            Instant.ofEpochMilli(System.currentTimeMillis() + jwtConfigurationService.getExpirationMillis())

        val hash = CryptoUtil.generateSaltedHash(userId.toHexString(), salt, "SHA-256")
        val payload = JwtPayload(userId, HexUtils.fromBytes(hash))

        val jwt = JWT()
            .setIssuer(jwtConfigurationService.getIssuer())
            .setSubject(jwtConfigurationService.getSubject())
            .setIssuedAt(ZonedDateTime.now())
            .addClaim("payload", payload)
            .setExpiration(
                ZonedDateTime
                    .ofInstant(expirationDate, ZoneOffset.UTC)
            )

        return JWT.getEncoder().encode(jwt, getSigner())
    }

    /**
     * Decode and verify a JWT
     */
    fun decodeToken(token: String): JWT? {
        return try {
            JWT.getDecoder().decode(token, getVerifier())
        } catch (ex: Exception) {
            when (ex) {
                is JWTUnavailableForProcessingException -> logger.error("Token Unavailable ${ex.message}")
                is InvalidJWTSignatureException -> logger.error("Invalid Token Signature: ${ex.message}")
                is JWTExpiredException -> logger.error("Token Expired: ${ex.message}")
                else -> logger.error("Invalid Token: ${ex.message}")
            }
            null
        }
    }

    /**
     * Attempts to decode a JWT and returns the payload.
     *
     * Throws an exception on cast failure or if the payload is invalid.
     */
    fun getPayload(token: JWT): JwtPayload {
        val claims = token.allClaims
        val payload = claims["payload"] as Map<*, *>?
            ?: throw Exception("Invalid Payload")

        val userId = payload["id"] as String? ?: throw Exception("Invalid Payload")
        val hash = payload["hash"] as String? ?: throw Exception("Invalid Payload")

        return JwtPayload(ObjectId(userId), hash)
    }

    /**
     * Attempts to decode a JWT and verify the payload.
     * Returns the user if successful, null otherwise.
     *
     * The user is verified by checking the hash in the payload against
     * the user's ID salted with the user's salt.
     *
     * @param principal The JWT cookie to verify
     */
    fun verifyToken(principal: String): User? {
        try {
            val token = decodeToken(principal) ?: return null
            val payload = getPayload(token)
            val user = userService.find(ObjectId(payload.id)) ?: return null
            val salt = vault.getUserSalt(user.id) ?: return null

            CryptoUtil.isHashValid(user, salt, payload.hash).let {
                if (!it) return null
                else return user
            }
        } catch (e: Exception) {
            logger.error("JwtService.verifyToken() - Error verifying token", e)
            return null
        }
    }

    fun getSigner(): HMACSigner = HMACSigner.newSHA256Signer(vault.getJwtSigningKey())
    fun getVerifier(): HMACVerifier = HMACVerifier.newVerifier(vault.getJwtSigningKey())

    fun getExpirationSeconds(): Int = jwtConfigurationService.getExpirationSeconds()
    fun getExpirationMillis(): Long = jwtConfigurationService.getExpirationMillis()
    fun getIssuer(): String = jwtConfigurationService.getIssuer()
    fun getSubject(): String = jwtConfigurationService.getSubject()
    fun getPrincipalName(): String = jwtConfigurationService.getPrincipalName()
}