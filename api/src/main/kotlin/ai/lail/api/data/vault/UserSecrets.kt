package ai.lail.api.data.vault

import com.bettercloud.vault.VaultException
import java.io.Serializable

class UserSecrets(secrets: Map<String, String>) : Serializable {
    val salt: String = secrets[SALT] ?: throw VaultException("Salt is invalid")

    fun toMap(): MutableMap<String, String> {
        return mutableMapOf(SALT to salt)
    }

    companion object {
        const val SALT = "salt"
    }
}