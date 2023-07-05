package ai.lail.api.util

import ai.lail.api.data.users.User
import io.fusionauth.jwt.HexUtils
import java.security.MessageDigest

object CryptoUtil {


    /**
     * Generates a random byte array of the given size.
     *
     * @param size The size of the byte array to generate.
     * @return A random byte array of the given size.
     */
    private fun randomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        val random = java.security.SecureRandom()
        random.nextBytes(bytes)
        return bytes
    }

    /**
     * Generates a random string of the given size.
     * Returned as a Hex encoded string.
     *
     * @param size The size of the string to generate.
     * @return A random string of the given size.
     */
    fun randomString(size: Int): String = HexUtils.fromBytes(randomBytes(size))

    /**
     * Generates an SHA-256 hash of the given string
     */
    fun generateHash(value: String, algo: String? = "SHA-256"): ByteArray? {
        return try {
            val digest = MessageDigest.getInstance(algo)
            digest.digest(value.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generates a salted SHA-256 hash
     *
     * @param value The value to hash
     * @param salt The salt to use
     */
    fun generateSaltedHash(value: String, salt: String, algo: String? = "SHA-256"): ByteArray? {
        return try {
            val md = MessageDigest.getInstance(algo)
            md.update(salt.toByteArray())
            val bytes = md.digest(value.toByteArray())
            md.reset()
            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isHashValid(userId: String, salt: String, hash: String): Boolean {
        val bytes = generateSaltedHash(userId, salt)
        return bytes != null && HexUtils.fromBytes(bytes) == hash
    }

    fun isHashValid(user: User, salt: String, hash: String): Boolean = isHashValid(user.id.toHexString(), salt, hash)

}